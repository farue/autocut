package de.farue.autocut.service.internetaccess;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;

import java.io.IOException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;

public class AutoReconnectingSshShell implements SshShell {

    private final SshConnection connection;

    private SimpleSshShell sshShell;
    private SSHClient sshClient;
    private Session session;

    public AutoReconnectingSshShell(SshConnection connection) {
        this.connection = connection;
    }

    @Override
    public synchronized String execute(String exec) throws IOException {
        return executeWithReconnect(exec, 1);
    }

    private String executeWithReconnect(String exec, int maxNumReconnects) throws IOException {
        if (sshShell == null || sshClient == null || !sshClient.isConnected()) {
            close();
            initConnection();
            maxNumReconnects--;
        }
        try {
            return sshShell.execute(exec);
        } catch (IOException e) {
            if (maxNumReconnects > 0 && e instanceof TransportException) {
                return executeWithReconnect(exec, maxNumReconnects - 1);
            } else {
                throw e;
            }
        }
    }

    public void close() throws IOException {
        if (sshShell != null) {
            sshShell.close();
        }
        if (session != null) {
            session.close();
        }
        if (sshClient != null) {
            sshClient.disconnect();
        }
    }

    protected void initConnection() throws IOException {
        OpenSSHKeyFile keyFile = new OpenSSHKeyFile();
        keyFile.init(connection.getPrivateKey(), connection.getPublicKey());
        sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(connection.getHostFingerprint());
        sshClient.connect(connection.getHostname());
        sshClient.authPublickey(connection.getUsername(), keyFile);
        session = sshClient.startSession();
        session.allocateDefaultPTY();
        Shell shell = session.startShell();

        Expect expect = new ExpectBuilder()
            .withOutput(shell.getOutputStream())
            .withInputs(shell.getInputStream(), shell.getErrorStream())
            .withInputFilters(removeColors(), removeNonPrintable())
            .withExceptionOnFailure()
            .build();

        sshShell = new SimpleSshShell(expect);
        if (connection.getPromptSymbol() != null) {
            sshShell.autoDetectPrompt(connection.getPromptSymbol());
        }
    }
}
