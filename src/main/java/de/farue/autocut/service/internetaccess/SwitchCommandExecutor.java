package de.farue.autocut.service.internetaccess;

import com.google.common.collect.Table;
import de.farue.autocut.domain.InternetAccess;
import java.io.Closeable;
import java.io.IOException;

public class SwitchCommandExecutor implements Closeable {

    private final SshShell sshShell;
    private final SwitchStatusParser statusParser;

    public SwitchCommandExecutor(SshShell sshShell) {
        this.sshShell = sshShell;
        this.statusParser = new SwitchStatusParser();
    }

    public void enable(InternetAccess internetAccess) throws SwitchCommandExecutionException {
        try {
            sshShell.execute("conf t");
            sshShell.execute("int " + internetAccess.getSwitchPortName());
            sshShell.execute("no shutdown");
            sshShell.execute("exit");
            sshShell.execute("exit");
        } catch (Exception e) {
            throw new SwitchCommandExecutionException(e);
        }
    }

    public void disable(InternetAccess internetAccess) throws SwitchCommandExecutionException {
        try {
            sshShell.execute("conf t");
            sshShell.execute("int " + internetAccess.getSwitchPortName());
            sshShell.execute("shutdown");
            sshShell.execute("exit");
            sshShell.execute("exit");
        } catch (Exception e) {
            throw new SwitchCommandExecutionException(e);
        }
    }

    public void saveConfig() throws SwitchCommandExecutionException {
        try {
            sshShell.execute("wr mem");
        } catch (Exception e) {
            throw new SwitchCommandExecutionException(e);
        }
    }

    public Table<String, String, String> getStatus() throws SwitchCommandExecutionException {
        try {
            sshShell.execute("terminal length 0");
            String interfaceStatusString = sshShell.execute("show int status");
            return statusParser.parse(interfaceStatusString);
        } catch (Exception e) {
            throw new SwitchCommandExecutionException(e);
        }
    }

    @Override
    public void close() throws IOException {
        sshShell.close();
    }
}
