package de.farue.autocut.service.accounting;

public class BookingBuilder {

    public static BookingTemplate.BookingTemplateBuilder bookingTemplate() {
        return BookingTemplate.builder();
    }

    public static TransactionTemplate.TransactionTemplateBuilder transactionTemplate() {
        return TransactionTemplate.builder();
    }

}
