package de.farue.autocut.config;

import de.farue.autocut.service.internetaccess.SshConnection;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Autocut.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Washit washit = new Washit();
    private final Banking banking = new Banking();
    private final List<SshConnection> switchConnections = new ArrayList<>();

    public Washit getWashit() {
        return washit;
    }

    public Banking getBanking() {
        return banking;
    }

    public List<SshConnection> getSwitchConnections() {
        return switchConnections;
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

    public static class Banking {

        private String name;
        private String iban;
        private String bic;
        private String blz;
        private String user;
        private String pin;
        private String passportPassword;
        private String tanCode;
        private String tanMedium;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIban() {
            return iban;
        }

        public void setIban(String iban) {
            this.iban = iban;
        }

        public String getBic() {
            return bic;
        }

        public void setBic(String bic) {
            this.bic = bic;
        }

        public String getBlz() {
            return blz;
        }

        public void setBlz(String blz) {
            this.blz = blz;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public String getPassportPassword() {
            return passportPassword;
        }

        public void setPassportPassword(String passportPassword) {
            this.passportPassword = passportPassword;
        }

        public String getTanCode() {
            return tanCode;
        }

        public void setTanCode(String tanCode) {
            this.tanCode = tanCode;
        }

        public String getTanMedium() {
            return tanMedium;
        }

        public void setTanMedium(String tanMedium) {
            this.tanMedium = tanMedium;
        }
    }
}
