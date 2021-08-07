package de.farue.autocut.service.accounting;

import com.google.common.math.DoubleMath;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.Tenant;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.utils.StringCandidateMatcher;
import de.farue.autocut.utils.StringCandidateMatcher.MatchResult;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAllTenantsBankTransactionMatcher implements BankTransactionMatcher {

    private static final String SPECIAL_CHARACTERS = ",.;:-_#+!\"§$%&/()=?{[]}\\*~€@";
    private static final int ERROR_RATIO_CUTOFF_PERCENT = 10;
    private static final double EPS = Math.ulp(1.0);

    private final Logger log = LoggerFactory.getLogger(AbstractAllTenantsBankTransactionMatcher.class);

    private final TenantService tenantService;
    private final LeaseService leaseService;
    private final MatchCandidateProvider matchCandidateProvider;

    protected AbstractAllTenantsBankTransactionMatcher(
        TenantService tenantService,
        LeaseService leaseService,
        MatchCandidateProvider matchCandidateProvider
    ) {
        this.tenantService = tenantService;
        this.leaseService = leaseService;
        this.matchCandidateProvider = matchCandidateProvider;
    }

    @Override
    public Optional<TransactionBook> findMatch(BankTransaction bankTransaction) {
        return findMatchByDistance(bankTransaction, tenantService.findAll()).map(leaseService::getCashTransactionBook);
    }

    protected abstract Optional<String> getMatchSentence(BankTransaction bankTransaction);

    private Optional<Lease> findMatchByDistance(BankTransaction bankTransaction, List<Tenant> tenants) {
        Optional<String> matchSentenceOptional = getMatchSentence(bankTransaction);
        if (matchSentenceOptional.isEmpty()) {
            return Optional.empty();
        }

        String matchSentence = matchSentenceOptional.get();
        String normalizedMatchSentence = normalize(matchSentence);

        double bestErrorRatio = Double.MAX_VALUE;
        Map<Lease, MatchResult> bestResultMap = new HashMap<>();
        for (Tenant tenant : tenants) {
            Set<String> candidates = matchCandidateProvider.buildMatchCandidates(tenant);
            Set<String> normalizedCandidates = candidates.stream().map(this::normalize).collect(Collectors.toSet());
            MatchResult matchResult = StringCandidateMatcher.findBestMatch(normalizedCandidates, normalizedMatchSentence);

            if (DoubleMath.fuzzyEquals(matchResult.errorRatio, bestErrorRatio, EPS)) {
                bestResultMap.put(tenant.getLease(), matchResult);
            } else if (matchResult.errorRatio < bestErrorRatio) {
                bestErrorRatio = matchResult.errorRatio;
                bestResultMap.clear();
                bestResultMap.put(tenant.getLease(), matchResult);
            }
        }

        if (bestResultMap.size() > 1) {
            // Try to find single match with longest candidate. Relevant in cases where two tenants
            // live in the same apartment and have the same last name (yes, that actually happens).
            // E.g. assume two tenants living in 123/1 with name "Li" and we receive a transaction
            // with purpose "123 1 chen li". The candidate "123 1 li" will be generated for both
            // tenants and perfectly matches a subset of the words, so it has an error ratio of 0.
            // The longer candidate generated for Chen Li is to be preferred.
            Map<Lease, MatchResult> newBestResultMap = new HashMap<>();
            int maxLength = 0;
            for (Entry<Lease, MatchResult> entry : bestResultMap.entrySet()) {
                int length = entry.getValue().candidate.length();
                if (length > maxLength) {
                    maxLength = length;
                    newBestResultMap.clear();
                    newBestResultMap.put(entry.getKey(), entry.getValue());
                } else if (length == maxLength) {
                    newBestResultMap.put(entry.getKey(), entry.getValue());
                }
            }
            bestResultMap = newBestResultMap;
        }

        Set<Lease> leases = bestResultMap.keySet();
        if (leases.size() > 1) {
            // At this point we give up trying to identify the lease
            log.info("Found multiple leases with tenants matching \"{}\": {}", normalizedMatchSentence, bestResultMap);
            return Optional.empty();
        }

        if (leases.size() == 1) {
            Lease lease = leases.iterator().next();
            MatchResult matchResult = bestResultMap.get(lease);

            // Check if error ratio is acceptable
            if (matchResult.errorRatio * 100 < ERROR_RATIO_CUTOFF_PERCENT) {
                return Optional.of(lease);
            }
        }

        return Optional.empty();
    }

    protected String normalize(String sentence) {
        sentence = StringUtils.lowerCase(sentence);

        // Replace all special characters with space
        sentence = StringUtils.replaceChars(sentence, SPECIAL_CHARACTERS, " ".repeat(SPECIAL_CHARACTERS.length()));

        return sentence;
    }
}
