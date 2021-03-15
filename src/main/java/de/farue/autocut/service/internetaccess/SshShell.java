package de.farue.autocut.service.internetaccess;

import java.io.Closeable;
import java.io.IOException;

public interface SshShell extends Closeable {

    String execute(String exec) throws IOException;
}
