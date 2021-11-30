package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Team;
import de.farue.autocut.repository.TeamRepository;
import de.farue.autocut.service.ContactService;
import de.farue.autocut.service.dto.ContactFormDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactResource {

    private final ContactService contactService;
    private final TeamRepository teamRepository;

    public ContactResource(ContactService contactService, TeamRepository teamRepository) {
        this.contactService = contactService;
        this.teamRepository = teamRepository;
    }

    @PostMapping("/{team}/email")
    public void sendEmail(@PathVariable(value = "team") final String team, @RequestBody ContactFormDTO contact) {
        String teamName = mapTeam(team);
        final Team t = teamRepository.findByName(teamName).orElseThrow();
        contactService.contact(t, contact);
    }

    private String mapTeam(String teamUrlPath) {
        return switch (teamUrlPath) {
            case "spokesperson" -> "SPOKESPERSON";
            case "assignment-team" -> "ASSIGNMENT";
            case "networking-team" -> "NETWORKING";
            case "washing-team" -> "WASHING";
            case "tools-team" -> "TOOLS";
            case "janitor" -> "JANITOR";
            default -> throw new IllegalArgumentException("Unknown team: " + teamUrlPath);
        };
    }
}
