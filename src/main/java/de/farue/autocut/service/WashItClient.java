package de.farue.autocut.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "${spring.application.name}", url = "${application.washit.url}")
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
