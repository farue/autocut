package de.farue.autocut.service.internetaccess;

import lombok.Data;

@Data
public class SshConnection {

    private String privateKey;
    private String publicKey;
    private String hostFingerprint;
    private String hostname;
    private int port = 22;
    private String username;
    private String promptSymbol;
}
