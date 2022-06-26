package de.farue.autocut.web.rest;

import de.farue.autocut.service.AdminService;
import de.farue.autocut.service.dto.MembershipDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminResource {

    private final AdminService adminService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public AdminResource(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/memberships")
    public List<MembershipDTO> getMemberships() {
        return adminService.getMemberships();
    }
}
