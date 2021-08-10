package de.farue.autocut.web.rest;

import de.farue.autocut.domain.BroadcastMessage;
import de.farue.autocut.service.BroadcastMessageService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicResource {

    private final BroadcastMessageService broadcastMessageService;

    public PublicResource(BroadcastMessageService broadcastMessageService) {
        this.broadcastMessageService = broadcastMessageService;
    }

    @GetMapping("/broadcast-messages")
    public List<BroadcastMessage> getBroadcastMessages() {
        return broadcastMessageService.findAllActive();
    }
}
