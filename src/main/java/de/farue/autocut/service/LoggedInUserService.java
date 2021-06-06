package de.farue.autocut.service;

import de.farue.autocut.domain.*;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.dto.UserDTO;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class LoggedInUserService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TenantService tenantService;
    private final WashHistoryService washHistoryService;
    private final NetworkSwitchStatusService networkSwitchStatusService;

    public LoggedInUserService(
        UserRepository userRepository,
        UserService userService,
        TenantService tenantService,
        WashHistoryService washHistoryService,
        NetworkSwitchStatusService networkSwitchStatusService
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.tenantService = tenantService;
        this.washHistoryService = washHistoryService;
        this.networkSwitchStatusService = networkSwitchStatusService;
    }

    public UserDTO getUser() {
        return userService.getUserWithAuthorities().map(UserDTO::new).orElseThrow();
    }

    public Tenant getTenant() {
        return SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantService::findOneByUser)
            .orElseThrow();
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

    public NetworkSwitchStatus getInternetStatus() {
        return networkSwitchStatusService.getSwitchInterfaceStatus(getInternetAccess());
    }

    public NetworkSwitchStatus updatedAndGetInternetStatus() {
        return networkSwitchStatusService.getSwitchInterfaceStatus(getInternetAccess(), Duration.ofSeconds(90));
    }
}
