package de.farue.autocut.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import tech.jhipster.config.JHipsterConstants;

@FeignClient(name = "${spring.application.name}", url = "${application.washit.url}/api/v1/washing")
@Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
public interface WashItClient {
    @PostMapping("/activate/{id}")
    void activate(@PathVariable("id") Integer id);

    @PostMapping("/enable-permanently/{id}")
    void enablePermanently(@PathVariable("id") Integer id);

    @PostMapping("/disable/{id}")
    void disable(@PathVariable("id") Integer id);

    @PostMapping("/disable-all")
    void disableAll();
}
