package de.farue.autocut.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(-1)
@Configuration
public class ServiceWorkerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .requestMatchers()
            .antMatchers("/ngsw-worker.js")
        .and()
            .headers()
            .contentSecurityPolicy("connect-src *; worker-src *;");
        // @formatter:on
    }
}
