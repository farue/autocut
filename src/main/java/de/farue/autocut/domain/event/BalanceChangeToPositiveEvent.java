package de.farue.autocut.domain.event;

import de.farue.autocut.domain.InternalTransaction;
import lombok.Data;

@Data
public class BalanceChangeToPositiveEvent {

    private final InternalTransaction previousTransaction;
    private final InternalTransaction currentTransaction;
    private final boolean outdated;
}
