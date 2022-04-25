package de.farue.autocut.web.rest;

import de.farue.autocut.domain.*;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.service.*;
import de.farue.autocut.service.accounting.TransactionBookService;
import de.farue.autocut.service.dto.AdminUserDTO;
import de.farue.autocut.service.dto.NetworkStatusDTO;
import de.farue.autocut.service.dto.TransactionBookDTO;
import de.farue.autocut.service.mapper.NetworkStatusMapper;
import de.farue.autocut.service.mapper.TransactionBookMapper;
import de.farue.autocut.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/me")
public class LoggedInUserResource {

    private static final String TIMESHEET_TIME_ENTITY_NAME = "timesheetTime";

    private final LaundryMachineService laundryMachineService;
    private final LoggedInUserService loggedInUserService;
    private final NetworkStatusMapper networkStatusMapper;
    private final TransactionBookService transactionBookService;
    private final TransactionBookMapper transactionBookMapper;
    private final TimesheetService timesheetService;
    private final TimesheetTimeService timesheetTimeService;
    private final TimesheetProjectService timesheetProjectService;
    private final TimesheetProjectMemberService timesheetProjectMemberService;
    private final TimesheetTaskService timesheetTaskService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public LoggedInUserResource(
        LaundryMachineService laundryMachineService,
        LoggedInUserService loggedInUserService,
        NetworkStatusMapper networkStatusMapper,
        TransactionBookService transactionBookService,
        TransactionBookMapper transactionBookMapper,
        TimesheetService timesheetService,
        TimesheetTimeService timesheetTimeService,
        TimesheetProjectService timesheetProjectService,
        TimesheetProjectMemberService timesheetProjectMemberService,
        TimesheetTaskService timesheetTaskService
    ) {
        this.laundryMachineService = laundryMachineService;
        this.loggedInUserService = loggedInUserService;
        this.networkStatusMapper = networkStatusMapper;
        this.transactionBookService = transactionBookService;
        this.transactionBookMapper = transactionBookMapper;
        this.timesheetService = timesheetService;
        this.timesheetTimeService = timesheetTimeService;
        this.timesheetProjectService = timesheetProjectService;
        this.timesheetProjectMemberService = timesheetProjectMemberService;
        this.timesheetTaskService = timesheetTaskService;
    }

    @GetMapping
    public AdminUserDTO getUser() {
        return loggedInUserService.getUser();
    }

    @GetMapping("/tenant")
    public Tenant getTenant() {
        return loggedInUserService.getTenant();
    }

    @GetMapping("/lease")
    public Lease getLease() {
        return loggedInUserService.getLease();
    }

    @GetMapping("/internet-access")
    public InternetAccess getInternetAccess() {
        return loggedInUserService.getInternetAccess();
    }

    @GetMapping("/lease/tenants")
    public Set<Tenant> getLeaseTenants() {
        return loggedInUserService.getLeaseTenants();
    }

    @GetMapping("/transaction-books")
    public List<TransactionBookDTO> getTransactionBooks() {
        return loggedInUserService
            .getTransactionBooks()
            .stream()
            // TODO: Deposit transaction books should be removed, then we won't need this filter anymore
            .filter(transactionBook -> transactionBook.getType() == TransactionBookType.CASH)
            .map(transactionBook -> {
                BigDecimal balance = transactionBookService.getCurrentBalance(transactionBook);
                return transactionBookMapper.fromTransactionBook(transactionBook, balance);
            })
            .collect(Collectors.toList());
    }

    @GetMapping("/transaction-books/{id}")
    public ResponseEntity<TransactionBookDTO> getTransactionBook(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(
            loggedInUserService
                .getTransactionBooks()
                .stream()
                .filter(transactionBook -> Objects.equals(transactionBook.getId(), id))
                .map(transactionBook -> {
                    BigDecimal balance = transactionBookService.getCurrentBalance(transactionBook);
                    return transactionBookMapper.fromTransactionBook(transactionBook, balance);
                })
                .findFirst()
        );
    }

    @GetMapping("/transaction-books/{id}/transactions")
    public List<InternalTransaction> getTransactions(
        @PathVariable Long id,
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant until,
        Pageable pageable
    ) {
        return loggedInUserService.getTransactions(id, from, until, pageable).getContent();
    }

    @GetMapping("/laundry-machines/{id}/history")
    public List<WashHistory> getWashHistory(@PathVariable Long id, Pageable pageable) {
        LaundryMachine machine = laundryMachineService.findOne(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return loggedInUserService.getWashHistory(machine, pageable).getContent();
    }

    @GetMapping("/laundry-machines/history")
    public List<WashHistory> getWashHistory(Pageable pageable) {
        return loggedInUserService.getWashHistory(pageable).getContent();
    }

    @GetMapping("/laundry-machines/{id}/suggestions")
    public List<LaundryProgram> getWashProgramSuggestions(@PathVariable Long id) {
        LaundryMachine machine = laundryMachineService.findOne(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return loggedInUserService.getWashProgramSuggestions(machine);
    }

    @GetMapping("/network/status")
    public NetworkStatusDTO getInternetStatus() {
        return networkStatusMapper.fromNetworkSwitchStatus(loggedInUserService.getInternetStatus());
    }

    @PostMapping("/network/update")
    public NetworkStatusDTO updatedAndGetInternetStatus() {
        return networkStatusMapper.fromNetworkSwitchStatus(loggedInUserService.updatedAndGetInternetStatus());
    }

    @GetMapping("/timesheets")
    public List<Timesheet> getTimesheets() {
        Tenant tenant = loggedInUserService.getTenant();
        return timesheetService.findOneByMember(tenant).stream().toList();
    }

    @GetMapping("/timesheets/{id}")
    public Timesheet getTimesheet(@PathVariable Long id) {
        Timesheet timesheet = getTimesheets()
            .stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return timesheet;
    }

    // TODO: This should be in TimesheetTimeResource but is here because we have to implement access rights first
    @GetMapping("/timesheets/{id}/times")
    public ResponseEntity<List<TimesheetTime>> getTimesheetTimes(@PathVariable Long id, Pageable pageable) {
        Timesheet timesheet = getTimesheets()
            .stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<TimesheetTime> page = timesheetTimeService.findAllByTimesheet(timesheet, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/timesheets/{id}/times")
    public ResponseEntity<TimesheetTime> createTimesheetTime(@PathVariable Long id, @RequestBody TimesheetTime time)
        throws URISyntaxException {
        if (time.getId() != null) {
            throw new BadRequestAlertException("A new timesheetTime cannot already have an ID", TIMESHEET_TIME_ENTITY_NAME, "idexists");
        }
        Timesheet timesheet = getTimesheets()
            .stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        time.setTimesheet(timesheet);

        TimesheetTime result = timesheetTimeService.saveWithValidation(time);
        return ResponseEntity.created(new URI("/api/timesheet-times/" + result.getId())).body(result);
    }

    @DeleteMapping("/timesheets/{timesheetId}/times/{timeId}")
    public ResponseEntity<Void> deleteTimesheetTime(@PathVariable Long timesheetId, @PathVariable Long timeId) {
        getTimesheets()
            .stream()
            .filter(t -> t.getId().equals(timesheetId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        timesheetTimeService.delete(timeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/timesheets/{id}/projects")
    public List<TimesheetProject> getTimesheetProjects(@PathVariable Long id) {
        Timesheet timesheet = getTimesheets()
            .stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return timesheetProjectService.findAllByTimesheetOrderByLastUsed(timesheet);
    }

    @GetMapping("/timesheets/{timesheetId}/projects/{projectId}/tasks")
    public List<TimesheetTask> getTimesheetProjectTasks(@PathVariable Long timesheetId, @PathVariable Long projectId) {
        Timesheet timesheet = getTimesheets()
            .stream()
            .filter(t -> t.getId().equals(timesheetId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TimesheetProject project = timesheetProjectMemberService
            .findAllByTimesheet(timesheet)
            .stream()
            .map(TimesheetProjectMember::getProject)
            .filter(p -> p.getId().equals(projectId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return timesheetTaskService.findAllByTimesheetProjectOrderByLastUsed(project);
    }

    @GetMapping("/timesheets/{timesheetId}/projects/{projectId}/descriptions")
    public List<String> getDescriptions(@PathVariable Long timesheetId, @PathVariable Long projectId) {
        Timesheet timesheet = getTimesheets()
            .stream()
            .filter(t -> t.getId().equals(timesheetId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TimesheetProject project = timesheetProjectMemberService
            .findAllByTimesheet(timesheet)
            .stream()
            .map(TimesheetProjectMember::getProject)
            .filter(p -> p.getId().equals(projectId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return timesheetTimeService.getDescriptions(timesheet, project);
    }
}
