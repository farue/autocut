package de.farue.autocut.service;

import de.farue.autocut.email.ImapFolderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;

@Service
@Profile("!" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
public class MailServiceMock extends MailService {

    private final Logger log = LoggerFactory.getLogger(MailServiceMock.class);

    public MailServiceMock(
        JHipsterProperties jHipsterProperties,
        JavaMailSenderImpl javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine,
        ImapFolderFactory imapFolderFactory
    ) {
        super(jHipsterProperties, javaMailSender, messageSource, templateEngine, imapFolderFactory);
    }

    @Override
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug(
            "Mocking send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );
    }
}
