package de.farue.autocut.service;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.Team;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.dto.ContactFormDTO;
import javax.mail.MessagingException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final MailService mailService;
    private final LoggedInUserService loggedInUserService;

    public ContactService(MailService mailService, LoggedInUserService loggedInUserService) {
        this.mailService = mailService;
        this.loggedInUserService = loggedInUserService;
    }

    public void contact(Team team, ContactFormDTO contact) {
        log.info("Sending contact message to team {} with data {}", team.getName(), contact);

        String customerEmail = contact.getEmail();
        String apartmentString = contact.getApartment();
        boolean isLoggedIn = SecurityUtils.isAuthenticated();
        if (isLoggedIn) {
            log.debug("User is logged in. Overwriting email and apartment with stored user data.");
            customerEmail = loggedInUserService.getUser().getEmail();
            apartmentString = buildApartmentString(loggedInUserService.getLease().getApartment());
        }

        String subject = buildSubject(apartmentString, contact.getSubject());

        MimeMessageHelper message = mailService.createMimeMessageHelper();
        try {
            message.setTo(team.getEmail());
            message.setSubject(subject);
            message.setReplyTo(customerEmail);
            message.setText(contact.getMessage());
        } catch (MessagingException e) {
            log.error("Failed to send email. Could not set parameter on MimeMessageHelper", e);
            throw new RuntimeException(e);
        }
        mailService.sendEmail(message.getMimeMessage());

        if (BooleanUtils.isTrue(contact.getCopyToOwnEmail())) {
            try {
                message.setReplyTo(team.getEmail());
                message.setTo(customerEmail);
            } catch (MessagingException e) {
                log.error("Failed to send email copy. Could not set parameter on MimeMessageHelper", e);
                throw new RuntimeException(e);
            }
            mailService.sendEmail(message.getMimeMessage());
        }
    }

    public String buildSubject(String apartment, String subject) {
        return StringUtils.isNotBlank(apartment) ? ("EPW " + apartment + ": " + subject) : subject;
    }

    private String buildApartmentString(Apartment apartment) {
        return apartment.getAddress().getStreetNumber() + "/" + apartment.getNr();
    }
}
