package de.farue.autocut.service.internetaccess;

import java.util.HashMap;
import java.util.Map;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;

public class SshSessionStore {

    private final Map<String, Session> sessionsByHost = new HashMap<>();

    private final SSHClient sshClient;

    public SshSessionStore(SSHClient sshClient) {
        this.sshClient = sshClient;
    }

    public Session getSession(String host) {
        if (sessionsByHost.containsKey(host)) {
            Session session = sessionsByHost.get(host);
            //            session.startShell()
        }
        return null;
    }
}
