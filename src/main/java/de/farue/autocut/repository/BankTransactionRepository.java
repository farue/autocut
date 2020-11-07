package de.farue.autocut.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.BankTransaction;

/**
 * Spring Data  repository for the BankTransaction entity.
 */
@Repository
public interface BankTransactionRepository extends TransactionRepository<BankTransaction> {

    List<BankTransaction> findAllByValueDate(Instant valueDate);

    List<BankTransaction> findAllByContraBankAccount(BankAccount contraBankAccount);
}
