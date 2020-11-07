package de.farue.autocut.batch.common;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import de.farue.autocut.service.accounting.BookingTemplate;
import de.farue.autocut.service.accounting.InternalTransactionService;

public class TenantFeeBatchWriter implements ItemWriter<BookingTemplate> {

    private InternalTransactionService transactionService;

    public TenantFeeBatchWriter(InternalTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void write(List<? extends BookingTemplate> bookingTemplates) throws Exception {
        for (BookingTemplate bookingTemplate : bookingTemplates) {
            transactionService.saveWithContraTransaction(bookingTemplate);
        }
    }
}
