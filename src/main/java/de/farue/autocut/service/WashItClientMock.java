package de.farue.autocut.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.github.jhipster.config.JHipsterConstants;

@Service
@Profile("!" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
public class WashItClientMock implements WashItClient {

    private final Logger log = LoggerFactory.getLogger(WashItClientMock.class);

    @Override
    public void activate(Integer id) {
        log.debug("Mocking activate machine {}", id);
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
