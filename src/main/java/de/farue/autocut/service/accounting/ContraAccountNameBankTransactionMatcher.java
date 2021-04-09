package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import java.util.Optional;

public class ContraAccountNameBankTransactionMatcher extends AbstractAllTenantsBankTransactionMatcher {

    public ContraAccountNameBankTransactionMatcher(
        TenantService tenantService,
        LeaseService leaseService,
        MatchCandidateProvider matchCandidateProvider
    ) {
        super(tenantService, leaseService, matchCandidateProvider);
    }

    @Override
    protected Optional<String> getMatchSentence(BankTransaction bankTransaction) {
        BankAccount contraBankAccount = bankTransaction.getContraBankAccount();
        if (contraBankAccount == null) {
            return Optional.empty();
        }
        String name = contraBankAccount.getName();
        return Optional.ofNullable(name);
    }
}
