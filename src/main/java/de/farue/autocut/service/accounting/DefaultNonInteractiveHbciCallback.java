package de.farue.autocut.service.accounting;

import java.util.Date;

import org.kapott.hbci.callback.AbstractHBCICallback;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultNonInteractiveHbciCallback extends AbstractHBCICallback {

    private final Logger log = LoggerFactory.getLogger(DefaultNonInteractiveHbciCallback.class);

    private String blz;
    private String user;
    private String pin;
    private String passportPassword;
    private String tanCode;
    private String tanMedium;

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

    @Override
    public void log(String msg, int level, Date date, StackTraceElement trace) {
        switch (level) {
            case HBCIUtils.LOG_DEBUG, HBCIUtils.LOG_DEBUG2 -> log.debug(msg);
            case HBCIUtils.LOG_INFO -> log.info(msg);
            case HBCIUtils.LOG_WARN -> log.warn(msg);
            case HBCIUtils.LOG_ERR -> log.error(msg);
        }
    }

    @Override
    public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData) {
        switch (reason) {
            case NEED_USERID, NEED_CUSTOMERID -> retData.replace(0, retData.length(), user);
            case NEED_PASSPHRASE_LOAD, NEED_PASSPHRASE_SAVE -> retData.replace(0, retData.length(), passportPassword);
            case NEED_PT_PIN -> retData.replace(0, retData.length(), pin);
            case NEED_BLZ -> retData.replace(0, retData.length(), blz);
            case NEED_PT_SECMECH -> retData.replace(0, retData.length(), tanCode);
            case NEED_PT_TAN -> throw new RuntimeException("tan input required");
            case NEED_PT_TANMEDIA -> retData.replace(0, retData.length(), tanMedium);
            case HAVE_ERROR -> log.error(msg);
            default -> log.info("Callback does not contain action for reason: " + reason);
        }
    }

    @Override
    public void status(HBCIPassport passport, int statusTag, Object[] o) {
        // ignore status messages for now
    }
}
