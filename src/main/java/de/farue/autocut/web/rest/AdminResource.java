package de.farue.autocut.web.rest;

import de.farue.autocut.service.AdminService;
import de.farue.autocut.service.dto.MembershipDTO;
import de.farue.autocut.service.dto.TeamDTO;
import de.farue.autocut.service.dto.TeamMembershipDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/teams")
    public List<TeamDTO> getTeams() {
        return adminService.getTeams();
    }

    @GetMapping("/teams/{id}")
    public TeamDTO getTeam(@PathVariable Long id) {
        return adminService.getTeam(id);
    }

    @GetMapping("/teams/{id}/members")
    public List<TeamMembershipDTO> getTeamMembers(@PathVariable Long id) {
        return adminService.getTeamMembers(id);
    }
}
