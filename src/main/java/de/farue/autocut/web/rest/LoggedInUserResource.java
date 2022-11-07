package de.farue.autocut.web.rest;

import de.farue.autocut.domain.*;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.*;
import de.farue.autocut.service.accounting.TransactionBookService;
import de.farue.autocut.service.dto.*;
import de.farue.autocut.service.mapper.NetworkStatusMapper;
import de.farue.autocut.service.mapper.TimesheetMapper;
import de.farue.autocut.service.mapper.TimesheetTimeMapper;
import de.farue.autocut.service.mapper.TransactionBookMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
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
    private final TimesheetTimerService timesheetTimerService;
    private final TimesheetTimeMapper timesheetTimeMapper;
    private final TimesheetMapper timesheetMapper;

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
        TimesheetTaskService timesheetTaskService,
        TimesheetTimerService timesheetTimerService,
        TimesheetTimeMapper timesheetTimeMapper,
        TimesheetMapper timesheetMapper
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
        this.timesheetTimerService = timesheetTimerService;
        this.timesheetTimeMapper = timesheetTimeMapper;
        this.timesheetMapper = timesheetMapper;
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
        return networkStatusMapper.fromNetworkSwitchStatus(loggedInUserService.getInternetStatus().orElse(null));
    }

    @PostMapping("/network/update")
    public NetworkStatusDTO updatedAndGetInternetStatus() {
        return networkStatusMapper.fromNetworkSwitchStatus(loggedInUserService.updatedAndGetInternetStatus().orElse(null));
    }

    @GetMapping("/timesheets")
    public List<TimesheetDTO> getTimesheets() {
        return getMyTimesheets().stream().map(t -> timesheetMapper.fromTimesheet(t, timesheetTimeService.getSumTime(t))).toList();
    }

    @GetMapping("/timesheets/{id}")
    public TimesheetDTO getTimesheet(@PathVariable Long id) {
        return getTimesheetById(id)
            .map(t -> timesheetMapper.fromTimesheet(t, timesheetTimeService.getSumTime(t)))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // TODO: This should be in TimesheetTimeResource but is here because we have to implement access rights first
    @GetMapping("/timesheets/{id}/times")
    public ResponseEntity<List<TimesheetTimeDTO>> getTimesheetTimes(@PathVariable Long id, Pageable pageable) {
        Timesheet timesheet = getTimesheetById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<TimesheetTime> page = timesheetTimeService.findAllByTimesheet(timesheet, pageable);

        boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);
        List<TimesheetTimeDTO> result = page
            .getContent()
            .stream()
            .map(time ->
                timesheetTimeMapper.fromTimesheetTime(
                    time,
                    // Admin users are not allowed to edit at the moment
                    !isAdmin && time.getStart().isAfter(Instant.now().minus(TimesheetTimeService.BOOKING_PERIOD))
                )
            )
            .toList();

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @PostMapping("/timesheets/{id}/times")
    public ResponseEntity<TimesheetTime> createTimesheetTime(@PathVariable Long id, @RequestBody @Valid CreateTimesheetTimeDTO timeDTO)
        throws URISyntaxException {
        TimesheetTime result = timesheetTimeService.save(id, null, timeDTO);
        if (timeDTO.isStopTimer()) {
            timesheetTimerService.deleteTimer();
        }
        return ResponseEntity.created(new URI("/api/timesheet-times/" + result.getId())).body(result);
    }

    @PutMapping("/timesheets/{timesheetId}/times/{timeId}")
    public ResponseEntity<TimesheetTime> updateTimesheetTime(
        @PathVariable Long timesheetId,
        @PathVariable Long timeId,
        @RequestBody @Valid CreateTimesheetTimeDTO timeDTO
    ) throws URISyntaxException {
        TimesheetTime result = timesheetTimeService.save(timesheetId, timeId, timeDTO);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/timesheets/{timesheetId}/times/{timeId}")
    public ResponseEntity<Void> deleteTimesheetTime(@PathVariable Long timesheetId, @PathVariable Long timeId) {
        getMyTimesheets()
            .stream()
            .filter(t -> t.getId().equals(timesheetId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        timesheetTimeService.delete(timeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/timesheets/{id}/projects")
    public List<TimesheetProject> getTimesheetProjects(@PathVariable Long id) {
        Timesheet timesheet = getMyTimesheets()
            .stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return timesheetProjectService.findAllByTimesheetOrderByLastUsed(timesheet);
    }

    @GetMapping("/timesheets/{timesheetId}/projects/{projectId}/tasks")
    public List<TimesheetTask> getTimesheetProjectTasks(@PathVariable Long timesheetId, @PathVariable Long projectId) {
        Timesheet timesheet = getTimesheetById(timesheetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
        Timesheet timesheet = getTimesheetById(timesheetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TimesheetProject project = timesheetProjectMemberService
            .findAllByTimesheet(timesheet)
            .stream()
            .map(TimesheetProjectMember::getProject)
            .filter(p -> p.getId().equals(projectId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return timesheetTimeService.getDescriptions(timesheet, project);
    }

    @GetMapping("/times/{id}")
    public TimesheetTime getTime(@PathVariable Long id) {
        return timesheetTimeService
            .findOne(id)
            .map(t -> {
                if (timesheetService.findOneForCurrentUser().map(timesheet -> t.getTimesheet().equals(timesheet)).orElse(false)) {
                    return t;
                } else {
                    return null;
                }
            })
            .orElse(null);
    }

    @GetMapping("/timesheets/timer")
    public TimesheetTimer getTimer() {
        return timesheetTimerService.getTimer();
    }

    @PostMapping("/timesheets/timer/start")
    public TimesheetTimer startTimer() {
        return timesheetTimerService.startTimer();
    }

    @PostMapping("/timesheets/timer/pause")
    public TimesheetTimer pauseTimer() {
        return timesheetTimerService.pauseTimer();
    }

    @PostMapping("/timesheets/timer/unpause")
    public TimesheetTimer unpauseTimer() {
        return timesheetTimerService.unpauseTimer();
    }

    @DeleteMapping("/timesheets/timer")
    public void deleteTimer() {
        timesheetTimerService.deleteTimer();
    }

    private Optional<Timesheet> getTimesheetById(Long id) {
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return timesheetService.findOne(id);
        } else {
            return getMyTimesheets().stream().filter(t -> Objects.equals(t.getId(), id)).findFirst();
        }
    }

    private List<Timesheet> getMyTimesheets() {
        Tenant tenant = loggedInUserService.getTenant();
        return timesheetService.findOneByMember(tenant).stream().toList();
    }
}
