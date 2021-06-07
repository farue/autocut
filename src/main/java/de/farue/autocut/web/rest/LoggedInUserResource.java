package de.farue.autocut.web.rest;

import de.farue.autocut.domain.*;
import de.farue.autocut.service.LaundryMachineService;
import de.farue.autocut.service.LoggedInUserService;
import de.farue.autocut.service.dto.NetworkStatusDTO;
import de.farue.autocut.service.dto.UserDTO;
import de.farue.autocut.service.mapper.NetworkStatusMapper;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/me")
public class LoggedInUserResource {

    private final LaundryMachineService laundryMachineService;
    private final LoggedInUserService loggedInUserService;
    private final NetworkStatusMapper networkStatusMapper;

    public LoggedInUserResource(
        LaundryMachineService laundryMachineService,
        LoggedInUserService loggedInUserService,
        NetworkStatusMapper networkStatusMapper
    ) {
        this.laundryMachineService = laundryMachineService;
        this.loggedInUserService = loggedInUserService;
        this.networkStatusMapper = networkStatusMapper;
    }

    @GetMapping
    public UserDTO getUser() {
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
    public List<TransactionBook> getTransactionBooks() {
        return loggedInUserService.getTransactionBooks();
    }

    @GetMapping("/transaction-books/{id}")
    public ResponseEntity<TransactionBook> getTransactionBook(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(
            loggedInUserService
                .getTransactionBooks()
                .stream()
                .filter(transactionBook -> Objects.equals(transactionBook.getId(), id))
                .findFirst()
        );
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
}
