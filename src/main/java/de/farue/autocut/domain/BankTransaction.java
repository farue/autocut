package de.farue.autocut.domain;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BankTransaction.
 */
@Entity
@Table(name = "bank_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BankTransaction extends Transaction {

    private static final long serialVersionUID = 1L;

    @Column(name = "customer_ref")
    private String customerRef;

    @Column(name = "gv_code")
    private String gvCode;

    @Column(name = "end_to_end")
    private String endToEnd;

    @Column(name = "primanota")
    private String primanota;

    @Column(name = "creditor")
    private String creditor;

    @Column(name = "mandate")
    private String mandate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "bankTransactions", allowSetters = true)
    private BankAccount bankAccount;

    @ManyToOne
    @JsonIgnoreProperties(value = "bankTransactions", allowSetters = true)
    private BankAccount contraBankAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    @Override
    public BankTransaction bookingDate(Instant bookingDate) {
        super.bookingDate(bookingDate);
        return this;
    }

    @Override
    public BankTransaction valueDate(Instant valueDate) {
        super.valueDate(valueDate);
        return this;
    }

    @Override
    public BankTransaction value(BigDecimal value) {
        super.value(value);
        return this;
    }

    @Override
    public BankTransaction balanceAfter(BigDecimal balanceAfter) {
        super.balanceAfter(balanceAfter);
        return this;
    }

    @Override
    public BankTransaction type(String type) {
        super.type(type);
        return this;
    }

    @Override
    public BankTransaction description(String description) {
        super.description(description);
        return this;
    }

    @Override
    public BankTransaction serviceQulifier(String serviceQulifier) {
        super.serviceQulifier(serviceQulifier);
        return this;
    }

    @Override
    public BankTransaction transactionBook(TransactionBook transactionBook) {
        super.transactionBook(transactionBook);
        return this;
    }

    public String getCustomerRef() {
        return this.customerRef;
    }

    public BankTransaction customerRef(String customerRef) {
        this.setCustomerRef(customerRef);
        return this;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public String getGvCode() {
        return this.gvCode;
    }

    public BankTransaction gvCode(String gvCode) {
        this.setGvCode(gvCode);
        return this;
    }

    public void setGvCode(String gvCode) {
        this.gvCode = gvCode;
    }

    public String getEndToEnd() {
        return this.endToEnd;
    }

    public BankTransaction endToEnd(String endToEnd) {
        this.setEndToEnd(endToEnd);
        return this;
    }

    public void setEndToEnd(String endToEnd) {
        this.endToEnd = endToEnd;
    }

    public String getPrimanota() {
        return this.primanota;
    }

    public BankTransaction primanota(String primanota) {
        this.setPrimanota(primanota);
        return this;
    }

    public void setPrimanota(String primanota) {
        this.primanota = primanota;
    }

    public String getCreditor() {
        return this.creditor;
    }

    public BankTransaction creditor(String creditor) {
        this.setCreditor(creditor);
        return this;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getMandate() {
        return this.mandate;
    }

    public BankTransaction mandate(String mandate) {
        this.setMandate(mandate);
        return this;
    }

    public void setMandate(String mandate) {
        this.mandate = mandate;
    }

    public BankAccount getBankAccount() {
        return this.bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BankTransaction bankAccount(BankAccount bankAccount) {
        this.setBankAccount(bankAccount);
        return this;
    }

    public BankAccount getContraBankAccount() {
        return this.contraBankAccount;
    }

    public void setContraBankAccount(BankAccount bankAccount) {
        this.contraBankAccount = bankAccount;
    }

    public BankTransaction contraBankAccount(BankAccount bankAccount) {
        this.setContraBankAccount(bankAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    // prettier-ignore
    @Override
    public String toString() {
        return "BankTransaction{" +
            "id=" + getId() +
            ", bookingDate='" + getBookingDate() + "'" +
            ", valueDate='" + getValueDate() + "'" +
            ", value=" + getValue() +
            ", balanceAfter=" + getBalanceAfter() +
            ", type='" + getType() + "'" +
            ", description='" + getDescription() + "'" +
            ", customerRef='" + getCustomerRef() + "'" +
            ", gvCode='" + getGvCode() + "'" +
            ", endToEnd='" + getEndToEnd() + "'" +
            ", primanota='" + getPrimanota() + "'" +
            ", creditor='" + getCreditor() + "'" +
            ", mandate='" + getMandate() + "'" +
            "}";
    }

    public boolean businessEquals(Object other) {
        if (this.equals(other)) {
            return true;
        }

        if (!(other instanceof BankTransaction)) {
            return false;
        }
        BankTransaction otherBankTransaction = (BankTransaction) other;
        return (
            Objects.equals(getBookingDate(), otherBankTransaction.getBookingDate()) &&
            Objects.equals(getValueDate(), otherBankTransaction.getValueDate()) &&
            compare(getValue()).isEqualTo(otherBankTransaction.getValue()) &&
            Objects.equals(getBankAccount(), otherBankTransaction.getBankAccount()) &&
            Objects.equals(getContraBankAccount(), otherBankTransaction.getContraBankAccount()) &&
            Objects.equals(getGvCode(), otherBankTransaction.getGvCode()) &&
            Objects.equals(getDescription(), otherBankTransaction.getDescription()) &&
            Objects.equals(getCustomerRef(), otherBankTransaction.getCustomerRef()) &&
            Objects.equals(getPrimanota(), otherBankTransaction.getPrimanota())
        );
    }
}
