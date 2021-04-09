package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.repository.BankTransactionRepository;
import de.farue.autocut.repository.TransactionRepository;
import de.farue.autocut.service.TransactionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankTransactionService extends TransactionService<BankTransaction> {

    private final BankTransactionRepository transactionRepository;
    private final InternalTransactionService internalTransactionService;
    private final BankTransactionContraTransactionProvider bankTransactionContraTransactionProvider;

    public BankTransactionService(
        BankTransactionRepository transactionRepository,
        InternalTransactionService internalTransactionService,
        BankTransactionContraTransactionProvider bankTransactionContraTransactionProvider
    ) {
        this.transactionRepository = transactionRepository;
        this.internalTransactionService = internalTransactionService;
        this.bankTransactionContraTransactionProvider = bankTransactionContraTransactionProvider;
    }

    @Override
    protected TransactionRepository<BankTransaction> getRepository() {
        return transactionRepository;
    }

    @Override
    protected void partialUpdate(BankTransaction existingTransaction, BankTransaction transaction) {
        if (transaction.getCustomerRef() != null) {
            existingTransaction.setCustomerRef(transaction.getCustomerRef());
        }
        if (transaction.getGvCode() != null) {
            existingTransaction.setGvCode(transaction.getGvCode());
        }
        if (transaction.getEndToEnd() != null) {
            existingTransaction.setEndToEnd(transaction.getEndToEnd());
        }
        if (transaction.getPrimanota() != null) {
            existingTransaction.setPrimanota(transaction.getPrimanota());
        }
        if (transaction.getCreditor() != null) {
            existingTransaction.setCreditor(transaction.getCreditor());
        }
        if (transaction.getMandate() != null) {
            existingTransaction.setMandate(transaction.getMandate());
        }
    }

    public void saveWithContraTransaction(BankTransaction transaction) {
        bankTransactionContraTransactionProvider
            .calculateContraTransaction(transaction)
            .ifPresent(
                contraTransaction -> {
                    contraTransaction.link(transaction);
                    internalTransactionService.save(contraTransaction);
                }
            );
        transactionRepository.save(transaction);
    }

    /**
     * Transactions do not immediately have an identifier so we can only check if and how many
     * transactions exist with the same characteristics.
     *
     * @param bankTransaction the bank transaction
     * @return number of transactions with the same characteristics
     */
    @Transactional(readOnly = true)
    public int countExisting(BankTransaction bankTransaction) {
        List<BankTransaction> transactions = transactionRepository.findAllByValueDate(bankTransaction.getValueDate());
        return (int) transactions.stream().filter(bankTransaction::businessEquals).count();
    }
}
