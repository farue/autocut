package de.farue.autocut.batch.fee;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.InternalTransactionService;

public class TenantFeeBatchWriter implements ItemWriter<BookingTemplate> {

    private final InternalTransactionService transactionService;

    public TenantFeeBatchWriter(InternalTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void write(List<? extends BookingTemplate> bookingTemplates) {
        for (BookingTemplate bookingTemplate : bookingTemplates) {
            transactionService.saveWithContraTransaction(bookingTemplate);
        }
    }
}
