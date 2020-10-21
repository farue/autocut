package de.farue.autocut.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.service.UserService;
import de.farue.autocut.service.dto.UserDTO;
import io.github.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/me")
public class LoggedInUserResource {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TenantService tenantService;

    public LoggedInUserResource(UserRepository userRepository, UserService userService, TenantService tenantService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.tenantService = tenantService;
    }

    @GetMapping
    public UserDTO getUser() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow();
    }

    @GetMapping("/tenant")
    public Tenant getTenant() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantService::findOneByUser)
            .orElseThrow();

    }

    @GetMapping("/lease")
    public Lease getLease() {
        return getTenant().getLease();
    }

    @GetMapping("/transaction-books")
    public List<TransactionBook> getTransactionBooks() {
        return new ArrayList<>(getLease().getTransactionBooks());
    }

    @GetMapping("/transaction-books/{id}")
    public ResponseEntity<TransactionBook> getTransactionBook(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(
            getLease().getTransactionBooks().stream()
                .filter(transactionBook -> Objects.equals(transactionBook.getId(), id))
                .findFirst());
    }
}
