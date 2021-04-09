package de.farue.autocut.service.accounting;

import com.google.common.base.Preconditions;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIVersion;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankingService {

    private static final HBCIVersion HBCI_VERSION = HBCIVersion.HBCI_300;
    private static final String HBCI_PASSPORT_INSTANCE = "PinTan";
    private static final String INIT_PASSPORT_TRUE = "1";
    private static final int PIN_TAN_SERVER_PORT = 443;

    private final Logger log = LoggerFactory.getLogger(BankingService.class);

    private final HBCICallback hbciCallback;

    private String blz;
    private String user;

    public BankingService(HBCICallback hbciCallback) {
        this.hbciCallback = hbciCallback;
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

    public BigDecimal getCurrentBalance() {
        List<HBCIJobResult> jobResults = execute(List.of("SaldoReq"));
        assert jobResults.size() == 1;

        GVRSaldoReq result = (GVRSaldoReq) jobResults.get(0);
        if (!result.isOK()) {
            throw new RuntimeException("Result: " + result.toString());
        }

        Value s = result.getEntries()[0].ready.value;
        return s.getBigDecimalValue();
    }

    public List<UmsLine> getTransactions() {
        List<HBCIJobResult> jobResults = execute(List.of("KUmsAll"));
        assert jobResults.size() == 1;

        GVRKUms result = (GVRKUms) jobResults.get(0);
        if (!result.isOK()) {
            throw new RuntimeException("Result: " + result.toString());
        }
        return result.getFlatData();
    }

    private List<HBCIJobResult> execute(List<String> jobNames) {
        Preconditions.checkNotNull(blz);
        Preconditions.checkNotNull(user);

        Properties props = new Properties();
        HBCIUtils.init(props, hbciCallback);
        final File passportFile = new File(getPassportName(blz, user));

        HBCIUtils.setParam("client.passport.default", HBCI_PASSPORT_INSTANCE);
        HBCIUtils.setParam("client.passport.PinTan.init", INIT_PASSPORT_TRUE);

        BankInfo info = HBCIUtils.getBankInfo(blz);

        HBCIPassport passport = AbstractHBCIPassport.getInstance(passportFile);
        passport.setCountry("DE");
        passport.setHost(info.getPinTanAddress());
        passport.setPort(PIN_TAN_SERVER_PORT);
        passport.setFilterType("Base64");

        HBCIHandler handle = null;

        try {
            // starts a new connection to the server
            handle = new HBCIHandler(HBCI_VERSION.getId(), passport);

            Konto[] konten = passport.getAccounts();
            if (konten == null || konten.length == 0) {
                throw new RuntimeException("Could not find any bank accounts");
            }

            Konto k = konten[0];

            List<HBCIJob> jobs = new ArrayList<>();
            for (String jobName : jobNames) {
                HBCIJob job = handle.newJob(jobName);
                job.setParam("my", k);
                job.addToQueue();
                jobs.add(job);
            }
            HBCIExecStatus status = handle.execute();

            // check if the communication with the bank worked
            if (!status.isOK()) {
                log.warn(status.toString());
            }

            return jobs.stream().map(HBCIJob::getJobResult).collect(Collectors.toList());
        } finally {
            if (handle != null) {
                handle.close();
            }
            passport.close();
        }
    }

    private String getPassportName(String blz, String user) {
        String name = "passport_" + blz + "_" + user + ".dat";
        return StringUtils.remove(name, '/');
    }
}
