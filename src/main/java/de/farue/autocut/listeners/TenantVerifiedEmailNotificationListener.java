package de.farue.autocut.listeners;

import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.event.TenantVerifiedEvent;
import de.farue.autocut.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TenantVerifiedEmailNotificationListener {

    private final Logger log = LoggerFactory.getLogger(TenantVerifiedEmailNotificationListener.class);

    private final MailService mailService;

    public TenantVerifiedEmailNotificationListener(MailService mailService) {
        this.mailService = mailService;
    }

    @EventListener
    public void handleTenantVerifiedEvent(TenantVerifiedEvent event) {
        log.debug("Got event: {}", event);

        Tenant tenant = event.getTenant();
        if (tenant.getUser() != null) {
            log.debug("Sending verification email");
            mailService.sendVerificationEmail(tenant.getUser());
        }
    }
}
