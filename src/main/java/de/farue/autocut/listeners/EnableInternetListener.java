package de.farue.autocut.listeners;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import de.farue.autocut.domain.event.BalanceChangeToPositiveEvent;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.NetworkSwitchService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

public class EnableInternetListener {

    private final Logger log = LoggerFactory.getLogger(TenantVerifiedEmailNotificationListener.class);

    private final LeaseService leaseService;
    private final NetworkSwitchService networkSwitchService;

    public EnableInternetListener(LeaseService leaseService, NetworkSwitchService networkSwitchService) {
        this.leaseService = leaseService;
        this.networkSwitchService = networkSwitchService;
    }

    @EventListener
    public void handleBalanceChangeToPositive(BalanceChangeToPositiveEvent event) {
        TransactionBook transactionBook = event.getCurrentTransaction().getTransactionBook();
        Optional
            .of(transactionBook)
            .filter(tb -> TransactionBookType.CASH.equals(tb.getType()))
            .flatMap(leaseService::findByTransactionBook)
            .map(l -> {
                log.debug(
                    "Balance for lease {} changed from {} to {} so internet access will be enabled again",
                    l,
                    event.getPreviousTransaction().getBalanceAfter(),
                    event.getCurrentTransaction().getBalanceAfter()
                );
                return l;
            })
            .map(Lease::getApartment)
            .map(Apartment::getInternetAccess)
            .ifPresent(networkSwitchService::enable);
    }
}
