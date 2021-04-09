package de.farue.autocut.devtools;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptEncryptor {

    private static final String PASSWORD = "";

    private static final String INPUT = "";

    public static void main(String[] args) {
        // see https://github.com/ulisesbocchio/jasypt-spring-boot
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(PASSWORD);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations(1000);
        config.setPoolSize(1);
        config.setProviderName("SunJCE");
        config.setProviderClassName(null);
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setConfig(config);
        String encrypted = encryptor.encrypt(INPUT);

        System.out.println("### ENCRYPTED OUTPUT ###");
        System.out.println(encrypted);
        System.out.println("########################");
    }
}
