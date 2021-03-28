package de.farue.autocut.config;

import java.util.List;

import org.kapott.hbci.callback.HBCICallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.TransactionBook;
import de.farue.autocut.repository.BankAccountRepository;
import de.farue.autocut.repository.BankTransactionRepository;
import de.farue.autocut.service.LeaseService;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.service.accounting.BankTransactionContraTransactionProvider;
import de.farue.autocut.service.accounting.BankTransactionMatcher;
import de.farue.autocut.service.accounting.BankTransactionService;
import de.farue.autocut.service.accounting.BankingService;
import de.farue.autocut.service.accounting.BankingServiceMock;
import de.farue.autocut.service.accounting.CompositeBankTransactionMatcher;
import de.farue.autocut.service.accounting.ContraAccountNameBankTransactionMatcher;
import de.farue.autocut.service.accounting.ContraAccountPreviousBookingBankTransactionMatcher;
import de.farue.autocut.service.accounting.DefaultNonInteractiveHbciCallback;
import de.farue.autocut.service.accounting.InternalBookingContraTransactionProvider;
import de.farue.autocut.service.accounting.MatchCandidateProvider;
import de.farue.autocut.service.accounting.PurposeBankTransactionMatcher;
import de.farue.autocut.service.accounting.TenantNameMatchCandidateProvider;
import de.farue.autocut.service.accounting.TenantPurposeMatchCandidateProvider;
import de.farue.autocut.service.accounting.TransactionBookService;
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
    public BankTransactionMatcher bankTransactionMatcher(PurposeBankTransactionMatcher purposeBankTransactionMatcher,
        ContraAccountPreviousBookingBankTransactionMatcher contraAccountPreviousBookingBankTransactionMatcher,
        ContraAccountNameBankTransactionMatcher contraAccountNameBankTransactionMatcher) {
        CompositeBankTransactionMatcher compositeBankTransactionMatcher = new CompositeBankTransactionMatcher();
        compositeBankTransactionMatcher.setMatchers(List.of(
            purposeBankTransactionMatcher,
            contraAccountPreviousBookingBankTransactionMatcher,
            contraAccountNameBankTransactionMatcher
        ));
        return compositeBankTransactionMatcher;
    }

    @Bean
    public PurposeBankTransactionMatcher purposeBankTransactionMatcher(TenantService tenantService, LeaseService leaseService,
        MatchCandidateProvider tenantPurposeMatchCandidateProvider) {
        return new PurposeBankTransactionMatcher(tenantService, leaseService, tenantPurposeMatchCandidateProvider);
    }

    @Bean
    public ContraAccountPreviousBookingBankTransactionMatcher contraAccountPreviousBookingBankTransactionMatcher(
        BankTransactionRepository bankTransactionRepository) {
        return new ContraAccountPreviousBookingBankTransactionMatcher(bankTransactionRepository);
    }

    @Bean
    public ContraAccountNameBankTransactionMatcher contraAccountNameBankTransactionMatcher(TenantService tenantService, LeaseService leaseService,
        MatchCandidateProvider tenantNameMatchCandidateProvider) {
        return new ContraAccountNameBankTransactionMatcher(tenantService, leaseService, tenantNameMatchCandidateProvider);
    }

    @Bean
    public BankTransactionContraTransactionProvider bankTransactionContraTransactionProvider(BankTransactionMatcher bankTransactionMatcher) {
        BankTransactionContraTransactionProvider contraTransactionProvider = new BankTransactionContraTransactionProvider(bankTransactionMatcher);
        contraTransactionProvider.setIssuer(BankTransactionService.class.getSimpleName());
        return contraTransactionProvider;
    }

    @Bean
    public InternalBookingContraTransactionProvider internalBookingContraTransactionProvider(TransactionBook referenceCashTransactionBook,
        TransactionBook referenceRevenueTransactionBook) {
        return new InternalBookingContraTransactionProvider(referenceCashTransactionBook, referenceRevenueTransactionBook);
    }

    @Bean
    @Lazy
    public TransactionBook referenceCashTransactionBook(TransactionBookService transactionBookService) {
        return transactionBookService.getOwnCashTransactionBook();
    }

    @Bean
    @Lazy
    public TransactionBook referenceRevenueTransactionBook(TransactionBookService transactionBookService) {
        return transactionBookService.getOwnRevenueTransactionBook();
    }

    @Bean
    @Lazy
    public BankAccount referenceBankAccount(BankAccountRepository bankAccountRepository) {
        return bankAccountRepository.findFirstByIban(applicationProperties.getBanking().getIban())
            .orElseGet(() -> bankAccountRepository.save(new BankAccount()
                .name(applicationProperties.getBanking().getName())
                .iban(applicationProperties.getBanking().getIban())
                .bic(applicationProperties.getBanking().getBic())));
    }
}
