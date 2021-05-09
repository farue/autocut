package de.farue.autocut.service;

import de.farue.autocut.service.dto.WashitActivateDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tech.jhipster.config.JHipsterConstants;

@Service
@Profile("!" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
public class WashItClientMock implements WashItClient {

    private static final int ACTIVATION_MS = 120000;

    private final Logger log = LoggerFactory.getLogger(WashItClientMock.class);

    @Override
    public WashitActivateDTO activate(Integer id) {
        log.debug("Mocking activate machine {}", id);
        return WashitActivateDTO
            .builder()
            .machineId(id)
            .activationTimestamp(Instant.now())
            .endActivationTime(Instant.now().plus(ACTIVATION_MS, ChronoUnit.MILLIS))
            .activationDurationMs(ACTIVATION_MS)
            .build();
    }

    @Override
    public void enablePermanently(Integer id) {
        log.debug("Mocking enable machine {} permanently", id);
    }

    @Override
    public void disable(Integer id) {
        log.debug("Mocking disable machine {}", id);
    }

    @Override
    public void disableAll() {
        log.debug("Mocking disable all machines");
    }
}
