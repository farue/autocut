package de.farue.autocut.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Autocut.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Washit washit = new Washit();

    public Washit getWashit() {
        return washit;
    }

    public static class Washit {

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
