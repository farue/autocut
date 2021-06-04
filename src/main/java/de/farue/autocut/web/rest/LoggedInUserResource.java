package de.farue.autocut.web.rest;

import de.farue.autocut.domain.*;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.*;
import de.farue.autocut.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/me")
public class LoggedInUserResource {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TenantService tenantService;
    private final LeaseService leaseService;
    private final WashHistoryService washHistoryService;
    private final LaundryMachineService laundryMachineService;

    public LoggedInUserResource(
        UserRepository userRepository,
        UserService userService,
        TenantService tenantService,
        LeaseService leaseService,
        WashHistoryService washHistoryService,
        LaundryMachineService laundryMachineService
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.tenantService = tenantService;
        this.leaseService = leaseService;
        this.washHistoryService = washHistoryService;
        this.laundryMachineService = laundryMachineService;
    }

    @GetMapping
    public UserDTO getUser() {
        return userService.getUserWithAuthorities().map(UserDTO::new).orElseThrow();
    }

    @GetMapping("/tenant")
    public Tenant getTenant() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantService::findOneByUser)
            .orElseThrow();
    }

    @GetMapping("/lease")
    public Lease getLease() {
        return getTenant().getLease();
    }

    @GetMapping("/lease/tenants")
    public Set<Tenant> getLeaseTenants() {
        return leaseService.findOne(getLease().getId()).orElseThrow().getTenants();
    }

    @GetMapping("/transaction-books")
    public List<TransactionBook> getTransactionBooks() {
        return new ArrayList<>(getLease().getTransactionBooks());
    }

    @GetMapping("/transaction-books/{id}")
    public ResponseEntity<TransactionBook> getTransactionBook(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(
            getLease().getTransactionBooks().stream().filter(transactionBook -> Objects.equals(transactionBook.getId(), id)).findFirst()
        );
    }

    @GetMapping("/laundry-machines/{id}/history")
    public List<WashHistory> getWashHistory(@PathVariable Long id, Pageable pageable) {
        Set<Tenant> tenants = getLeaseTenants();
        return laundryMachineService
            .findOne(id)
            .map(machine -> washHistoryService.getWashHistory(tenants, machine, pageable))
            .map(Slice::getContent)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/laundry-machines/history")
    public List<WashHistory> getWashHistory(Pageable pageable) {
        Set<Tenant> tenants = getLeaseTenants();
        return washHistoryService.getWashHistory(tenants, pageable).getContent();
    }

    @GetMapping("/laundry-machines/{id}/suggestions")
    public List<LaundryProgram> getWashProgramSuggestions(@PathVariable Long id) {
        Set<Tenant> tenants = getLeaseTenants();
        return laundryMachineService
            .findOne(id)
            .stream()
            .flatMap(machine -> washHistoryService.findSuggestions(tenants, machine).stream())
            .collect(Collectors.toList());
    }
}
