package de.farue.autocut.service.accounting;

import java.time.Instant;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
public class BookingTemplate {

    @Singular
    private Set<TransactionTemplate> transactionTemplates;

    private Instant bookingDate;

    private Instant valueDate;
}
