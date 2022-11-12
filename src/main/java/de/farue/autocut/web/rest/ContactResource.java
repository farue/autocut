package de.farue.autocut.web.rest;

import de.farue.autocut.domain.Team;
import de.farue.autocut.repository.TeamRepository;
import de.farue.autocut.service.ContactService;
import de.farue.autocut.service.dto.ContactFormDTO;
import org.apache.commons.lang3.StringUtils;
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
    public void sendEmail(@PathVariable(value = "team") String team, @RequestBody ContactFormDTO contact) {
        team = StringUtils.remove(team, "-team");
        final Team t = teamRepository.findByName(team).orElseThrow();
        contactService.contact(t, contact);
    }
}
