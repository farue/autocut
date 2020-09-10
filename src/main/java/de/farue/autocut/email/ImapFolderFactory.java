package de.farue.autocut.email;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class ImapFolderFactory {

    private JavaMailSenderImpl javaMailSender;

    public ImapFolderFactory(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public ImapFolder getFolder(String folderName) throws MessagingException {
        Session session = javaMailSender.getSession();
        Store store = session.getStore("imap");
        if (!store.isConnected()) {
            store.connect(javaMailSender.getHost(), javaMailSender.getUsername(), javaMailSender.getPassword());
        }
        Folder folder = store.getFolder(folderName);
        return new ImapFolder(folder);
    }
}
