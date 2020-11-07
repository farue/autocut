package de.farue.autocut.domain;

import static de.farue.autocut.utils.BigDecimalUtil.compare;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A BankTransaction.
 */
@Entity
@Table(name = "bank_transaction")
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
        return customerRef;
    }

    public BankTransaction customerRef(String customerRef) {
        this.customerRef = customerRef;
        return this;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public String getGvCode() {
        return gvCode;
    }

    public BankTransaction gvCode(String gvCode) {
        this.gvCode = gvCode;
        return this;
    }

    public void setGvCode(String gvCode) {
        this.gvCode = gvCode;
    }

    public String getEndToEnd() {
        return endToEnd;
    }

    public BankTransaction endToEnd(String endToEnd) {
        this.endToEnd = endToEnd;
        return this;
    }

    public void setEndToEnd(String endToEnd) {
        this.endToEnd = endToEnd;
    }

    public String getPrimanota() {
        return primanota;
    }

    public BankTransaction primanota(String primanota) {
        this.primanota = primanota;
        return this;
    }

    public void setPrimanota(String primanota) {
        this.primanota = primanota;
    }

    public String getCreditor() {
        return creditor;
    }

    public BankTransaction creditor(String creditor) {
        this.creditor = creditor;
        return this;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getMandate() {
        return mandate;
    }

    public BankTransaction mandate(String mandate) {
        this.mandate = mandate;
        return this;
    }

    public void setMandate(String mandate) {
        this.mandate = mandate;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public BankTransaction bankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
        return this;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public BankAccount getContraBankAccount() {
        return contraBankAccount;
    }

    public BankTransaction contraBankAccount(BankAccount bankAccount) {
        this.contraBankAccount = bankAccount;
        return this;
    }

    public void setContraBankAccount(BankAccount bankAccount) {
        this.contraBankAccount = bankAccount;
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
        return Objects.equals(getBookingDate(), otherBankTransaction.getBookingDate())
            && Objects.equals(getValueDate(), otherBankTransaction.getValueDate())
            && compare(getValue()).isEqualTo(otherBankTransaction.getValue())
            && Objects.equals(getBankAccount(), otherBankTransaction.getBankAccount())
            && Objects.equals(getContraBankAccount(), otherBankTransaction.getContraBankAccount())
            && Objects.equals(getType(), otherBankTransaction.getType())
            && Objects.equals(getDescription(), otherBankTransaction.getDescription())
            && Objects.equals(getCustomerRef(), otherBankTransaction.getCustomerRef())
            && Objects.equals(getPrimanota(), otherBankTransaction.getPrimanota());
    }
}
