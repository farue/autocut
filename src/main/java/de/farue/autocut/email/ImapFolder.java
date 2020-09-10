package de.farue.autocut.email;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

public class ImapFolder {

    public static final String SENT_ITEMS = "Sent Items";

    private final Folder folder;

    public ImapFolder(Folder folder) {
        this.folder = folder;
    }

    public synchronized void appendMessages(Message... messages) throws MessagingException {
        Store store = folder.getStore();
        if (!store.isConnected()) {
            throw new IllegalStateException("Store needs to be connected.");
        }

        if (!folder.isOpen()) {
            folder.open(Folder.READ_WRITE);
        }

        for (Message message : messages) {
            message.setFlag(Flag.SEEN, true);
        }
        folder.appendMessages(messages);
    }
}
