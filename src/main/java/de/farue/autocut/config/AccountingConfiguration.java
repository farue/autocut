package de.farue.autocut.config;

import de.farue.autocut.repository.BankTransactionRepository;
import de.farue.autocut.service.AssociationService;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.service.accounting.*;
import java.util.List;
import org.kapott.hbci.callback.HBCICallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import tech.jhipster.config.JHipsterConstants;

@Configuration
public class AccountingConfiguration {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    Environment env;

    @Bean
    @Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
    public BankingService bankingService(HBCICallback hbciCallback) {
        BankingService bankingService = new BankingService(hbciCallback);
        bankingService.setBlz(applicationProperties.getBanking().getBlz());
        bankingService.setUser(applicationProperties.getBanking().getUser());
        return bankingService;
    }

    @Bean
    @Profile("!" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
    public BankingServiceMock bankingServiceMock() {
        return new BankingServiceMock();
    }

    @Bean
    @Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
    public DefaultNonInteractiveHbciCallback hbciCallback() {
        DefaultNonInteractiveHbciCallback hbciCallback = new DefaultNonInteractiveHbciCallback();
        hbciCallback.setBlz(applicationProperties.getBanking().getBlz());
        hbciCallback.setUser(applicationProperties.getBanking().getUser());
        hbciCallback.setPin(applicationProperties.getBanking().getPin());
        hbciCallback.setPassportPassword(applicationProperties.getBanking().getPassportPassword());
        hbciCallback.setTanCode(applicationProperties.getBanking().getTanCode());
        hbciCallback.setTanMedium(applicationProperties.getBanking().getTanMedium());
        return hbciCallback;
    }

    @Bean
    public TenantPurposeMatchCandidateProvider tenantPurposeMatchCandidateProvider() {
        return new TenantPurposeMatchCandidateProvider();
    }

    @Bean
    public TenantNameMatchCandidateProvider tenantNameMatchCandidateProvider() {
        return new TenantNameMatchCandidateProvider();
    }

    @Bean
    public BankTransactionMatcher bankTransactionMatcher(
        PurposeBankTransactionMatcher purposeBankTransactionMatcher,
        ContraAccountPreviousBookingBankTransactionMatcher contraAccountPreviousBookingBankTransactionMatcher,
        ContraAccountNameBankTransactionMatcher contraAccountNameBankTransactionMatcher
    ) {
        CompositeBankTransactionMatcher compositeBankTransactionMatcher = new CompositeBankTransactionMatcher();
        compositeBankTransactionMatcher.setMatchers(
            List.of(
                purposeBankTransactionMatcher,
                contraAccountPreviousBookingBankTransactionMatcher,
                contraAccountNameBankTransactionMatcher
            )
        );
        return compositeBankTransactionMatcher;
    }

    @Bean
    public PurposeBankTransactionMatcher purposeBankTransactionMatcher(
        TenantService tenantService,
        LeaseService leaseService,
        MatchCandidateProvider tenantPurposeMatchCandidateProvider
    ) {
        return new PurposeBankTransactionMatcher(tenantService, leaseService, tenantPurposeMatchCandidateProvider);
    }

    @Bean
    public ContraAccountPreviousBookingBankTransactionMatcher contraAccountPreviousBookingBankTransactionMatcher(
        BankTransactionRepository bankTransactionRepository
    ) {
        return new ContraAccountPreviousBookingBankTransactionMatcher(bankTransactionRepository);
    }

    @Bean
    public ContraAccountNameBankTransactionMatcher contraAccountNameBankTransactionMatcher(
        TenantService tenantService,
        LeaseService leaseService,
        MatchCandidateProvider tenantNameMatchCandidateProvider
    ) {
        return new ContraAccountNameBankTransactionMatcher(tenantService, leaseService, tenantNameMatchCandidateProvider);
    }

    @Bean
    public BankTransactionContraTransactionProvider bankTransactionContraTransactionProvider(
        BankTransactionMatcher bankTransactionMatcher
    ) {
        BankTransactionContraTransactionProvider contraTransactionProvider = new BankTransactionContraTransactionProvider(
            bankTransactionMatcher
        );
        contraTransactionProvider.setIssuer(BankTransactionService.class.getSimpleName());
        return contraTransactionProvider;
    }

    @Bean
    public InternalBookingContraTransactionProvider internalBookingContraTransactionProvider(AssociationService associationService) {
        return new InternalBookingContraTransactionProvider(associationService);
    }
}
