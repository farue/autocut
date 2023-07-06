package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class PurposeBankTransactionMatcher extends AbstractAllTenantsBankTransactionMatcher {

    private static final String VERWENDUNGSZWECK = "svwz+";

    public PurposeBankTransactionMatcher(
        TenantService tenantService,
        LeaseService leaseService,
        MatchCandidateProvider matchCandidateProvider
    ) {
        super(tenantService, leaseService, matchCandidateProvider);
    }

    @Override
    protected Optional<String> getMatchSentence(BankTransaction bankTransaction) {
        String purpose = bankTransaction.getDescription();
        if (StringUtils.isEmpty(purpose)) {
            return Optional.empty();
        }

        return Optional.of(purpose);
    }

    @Override
    protected String normalize(String purpose) {
        purpose = super.normalize(purpose);

        // To increase performance of the following algorithm, we cut a few words
        if (StringUtils.contains(purpose, VERWENDUNGSZWECK)) {
            purpose = StringUtils.substringAfter(purpose, VERWENDUNGSZWECK);
        }

        return purpose;
    }
}
