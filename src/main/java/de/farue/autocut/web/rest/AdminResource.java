package de.farue.autocut.web.rest;

import de.farue.autocut.service.AdminService;
import de.farue.autocut.service.CompensationService;
import de.farue.autocut.service.dto.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminResource {

    private final AdminService adminService;
    private final CompensationService compensationService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public AdminResource(AdminService adminService, CompensationService compensationService) {
        this.adminService = adminService;
        this.compensationService = compensationService;
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

    @PostMapping("/timesheets/compensation")
    public List<CompensationDTO> calculateCompensations(@RequestBody TimeRangeDTO timeRange) {
        return compensationService.calculateCompensations(timeRange.getEarliest(), timeRange.getLatest());
    }
}
