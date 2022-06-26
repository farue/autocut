package de.farue.autocut.service;

import de.farue.autocut.domain.InternalTransaction;
import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryProgram;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.WashHistory;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.accounting.InternalTransactionService;
import de.farue.autocut.service.dto.AdminUserDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@Service
public class LoggedInUserService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TenantService tenantService;
    private final WashHistoryService washHistoryService;
    private final NetworkSwitchStatusService networkSwitchStatusService;
    private final InternalTransactionService transactionService;

    public LoggedInUserService(
        UserRepository userRepository,
        UserService userService,
        TenantService tenantService,
        WashHistoryService washHistoryService,
        NetworkSwitchStatusService networkSwitchStatusService,
        InternalTransactionService transactionService
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.tenantService = tenantService;
        this.washHistoryService = washHistoryService;
        this.networkSwitchStatusService = networkSwitchStatusService;
        this.transactionService = transactionService;
    }

    public AdminUserDTO getUser() {
        return userService.getUserWithAuthorities().map(AdminUserDTO::new).orElseThrow();
    }

    public Tenant getTenant() {
        return getTenantOptional().orElseThrow();
    }

    public Optional<Tenant> getTenantOptional() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).flatMap(tenantService::findOneByUser);
    }

    public Lease getLease() {
        return getTenant().getLease();
    }

    public InternetAccess getInternetAccess() {
        return getLease().getApartment().getInternetAccess();
    }

    public Set<Tenant> getLeaseTenants() {
        return getLease().getTenants();
    }

    public List<TransactionBook> getTransactionBooks() {
        return new ArrayList<>(getLease().getTransactionBooks());
    }

    public TransactionBook getTransactionBook(Long id) {
        return getTransactionBooks()
            .stream()
            .filter(transactionBook -> Objects.equals(transactionBook.getId(), id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Page<InternalTransaction> getTransactions(Long id, Instant from, Instant until, Pageable pageable) {
        TransactionBook transactionBook = getTransactionBook(id);
        return transactionService.findAllForTransactionBook(transactionBook, from, until, pageable);
    }

    public Page<WashHistory> getWashHistory(LaundryMachine machine, Pageable pageable) {
        Set<Tenant> tenants = getLeaseTenants();
        return washHistoryService.getWashHistory(tenants, machine, pageable);
    }

    public Page<WashHistory> getWashHistory(Pageable pageable) {
        Set<Tenant> tenants = getLeaseTenants();
        return washHistoryService.getWashHistory(tenants, pageable);
    }

    public List<LaundryProgram> getWashProgramSuggestions(LaundryMachine machine) {
        Set<Tenant> tenants = getLeaseTenants();
        return washHistoryService.findSuggestions(tenants, machine);
    }

    public Optional<NetworkSwitchStatus> getInternetStatus() {
        return networkSwitchStatusService.getSwitchInterfaceStatus(getInternetAccess());
    }

    public Optional<NetworkSwitchStatus> updatedAndGetInternetStatus() {
        return networkSwitchStatusService.getSwitchInterfaceStatus(getInternetAccess(), Duration.ofSeconds(90));
    }
}
