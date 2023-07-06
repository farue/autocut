package de.farue.autocut.batch.banking;

import com.google.common.base.Joiner;
import de.farue.autocut.domain.BankAccount;
import de.farue.autocut.domain.BankTransaction;
import de.farue.autocut.repository.BankAccountRepository;
import de.farue.autocut.service.AssociationService;
import de.farue.autocut.service.accounting.BankTransactionService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.structures.Konto;
import org.springframework.batch.item.ItemProcessor;

public class BankingBatchProcessor implements ItemProcessor<UmsLine, BankTransaction> {

    private final BankAccountRepository bankAccountRepository;
    private final BankTransactionService bankTransactionService;
    private final AssociationService associationService;

    private final Map<BankTransaction, Integer> transactionCounterMap = new HashMap<>();

    public BankingBatchProcessor(
        BankAccountRepository bankAccountRepository,
        BankTransactionService bankTransactionService,
        AssociationService associationService
    ) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransactionService = bankTransactionService;
        this.associationService = associationService;
    }

    @Override
    public BankTransaction process(UmsLine transaction) {
        BankTransaction bankTransaction = new BankTransaction()
            .bookingDate(transaction.bdate.toInstant())
            .valueDate(transaction.valuta.toInstant())
            .value(transaction.value.getBigDecimalValue())
            .balanceAfter(transaction.saldo.value.getBigDecimalValue())
            .description(getDescription(transaction))
            .customerRef(transaction.customerref)
            .gvCode(transaction.gvcode)
            .type(transaction.text != null ? transaction.text : getType(transaction.gvcode))
            .endToEnd(transaction.endToEndId)
            .primanota(transaction.primanota)
            .creditor(transaction.id)
            .bankAccount(associationService.getBankAccount())
            .contraBankAccount(getContraBankAccount(transaction).orElse(null))
            .transactionBook(associationService.getCashTransactionBook());

        transactionCounterMap.computeIfAbsent(bankTransaction, bankTransactionService::countExisting);
        int count = transactionCounterMap.get(bankTransaction);
        if (count > 0) {
            // a transaction with the same characteristics already exists, so just decrement the counter and skip this transaction
            transactionCounterMap.put(bankTransaction, count - 1);
            return null;
        } else {
            return bankTransaction;
        }
    }

    private String getType(String gvCode) {
        return switch (gvCode) {
            case "082" -> "EINZAHLUNG";
            case "105" -> "SDD LASTSCHR";
            case "106" -> "KARTENZAHLUNG";
            case "116" -> "SEPA ÜBERW.";
            case "152" -> "D GUT SEPA";
            case "166" -> "GUTSCHR.SEPA";
            case "168" -> "ECHTZEITÜBERW GUTSCHRIFT";
            case "805" -> "ZINSEN/ENTG.";
            case "808" -> "ENTGELT";
            default -> "UNBEKANNT";
        };
    }

    private Optional<BankAccount> getContraBankAccount(UmsLine transaction) {
        Konto contraKonto = transaction.other;

        if (contraKonto == null) {
            return Optional.empty();
        }

        String iban = StringUtils.deleteWhitespace(contraKonto.iban);
        String bic = StringUtils.deleteWhitespace(contraKonto.bic);
        String name = Joiner.on(" ").skipNulls().join(contraKonto.name, contraKonto.name2);

        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(iban) || StringUtils.isEmpty(bic)) {
            return Optional.empty();
        }

        return Optional.of(
            bankAccountRepository
                .findFirstByIban(iban)
                .orElseGet(() -> bankAccountRepository.save(new BankAccount().name(name).iban(iban).bic(bic)))
        );
    }

    private String getDescription(UmsLine transaction) {
        StringBuilder description = new StringBuilder();
        lines:for (String line : transaction.usage) {
            if (description.isEmpty()) {
                description.append(line);
                continue;
            }
            if (line.contains("+")) {
                for (MessageParts part : MessageParts.values()) {
                    if (line.startsWith(part.getCode() + "+")) {
                        description.append(" ").append(line);
                        continue lines;
                    }
                }
            }
            description.append(line);
        }
        return description.toString();
    }

    private String getMessagePart(UmsLine transaction, MessageParts messagePart) {
        StringBuilder sb = new StringBuilder();
        boolean messageContinue = false;
        boolean found = false;
        for (String line : transaction.usage) {
            if (line.startsWith(messagePart.getCode() + "+")) {
                sb.append(line.substring(messagePart.getCode().length() + 1));
                messageContinue = true;
                found = true;
            } else if (messageContinue) {
                for (MessageParts otherPart : MessageParts.values()) {
                    if (line.startsWith(otherPart.getCode() + "+")) {
                        messageContinue = false;
                        break;
                    }
                }
                sb.append(line);
            }
        }
        if (found) {
            return sb.toString();
        }
        return null;
    }

    private enum MessageParts {
        END_TO_END_REFERENCE("EREF"),
        CUSTOMER_REFERENCE("KREF"),
        MANDATE_REFERENCE("MREF"),
        BANK_REFERENCE("BREF"),
        RETURN_REFERENCE("RREF"),
        CREDITOR_ID("CRED"),
        DEBITOR_ID("DEBT"),
        INTEREST_COMPENSATION_AMOUNT("COAM"),
        ORIGINAL_TRANSACTION_AMOUNT("OAMT"),
        PURPOSE("SVWZ"),
        ALTERNATIVE_ISSUER("ABWA"),
        ALTERNATIVE_RECIPIENT("ABWE"),
        IBAN("IBAN"),
        BIC("BIC");

        private final String code;

        MessageParts(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
