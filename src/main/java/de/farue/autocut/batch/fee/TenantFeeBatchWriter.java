package de.farue.autocut.batch.fee;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.TransactionBookService;

public class TenantFeeBatchWriter implements ItemWriter<BookingTemplate> {

    private TransactionBookService transactionBookService;

    public TenantFeeBatchWriter(TransactionBookService transactionBookService) {
        this.transactionBookService = transactionBookService;
    }

    @Override
    public void write(List<? extends BookingTemplate> bookingTemplates) throws Exception {
        for (BookingTemplate bookingTemplate : bookingTemplates) {
            transactionBookService.saveMemberBooking(bookingTemplate);
        }
    }
}
