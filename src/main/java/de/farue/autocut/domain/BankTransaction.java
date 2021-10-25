package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BankTransaction.
 */
@Entity
@Table(name = "bank_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BankTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "booking_date", nullable = false)
    private Instant bookingDate;

    @NotNull
    @Column(name = "value_date", nullable = false)
    private Instant valueDate;

    @NotNull
    @Column(name = "value", precision = 21, scale = 2, nullable = false)
    private BigDecimal value;

    @NotNull
    @Column(name = "balance_after", precision = 21, scale = 2, nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

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
    private BankAccount bankAccount;

    @ManyToOne
    private BankAccount contraBankAccount;

    @ManyToMany
    @JoinTable(
        name = "rel_bank_transaction__left",
        joinColumns = @JoinColumn(name = "bank_transaction_id"),
        inverseJoinColumns = @JoinColumn(name = "left_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "bankAccount", "contraBankAccount", "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<BankTransaction> lefts = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "iTransactions", "bTransactions", "leases" }, allowSetters = true)
    private TransactionBook transactionBook;

    @ManyToMany(mappedBy = "lefts")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "bankAccount", "contraBankAccount", "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<BankTransaction> rights = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BankTransaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getBookingDate() {
        return this.bookingDate;
    }

    public BankTransaction bookingDate(Instant bookingDate) {
        this.setBookingDate(bookingDate);
        return this;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Instant getValueDate() {
        return this.valueDate;
    }

    public BankTransaction valueDate(Instant valueDate) {
        this.setValueDate(valueDate);
        return this;
    }

    public void setValueDate(Instant valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public BankTransaction value(BigDecimal value) {
        this.setValue(value);
        return this;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getBalanceAfter() {
        return this.balanceAfter;
    }

    public BankTransaction balanceAfter(BigDecimal balanceAfter) {
        this.setBalanceAfter(balanceAfter);
        return this;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getType() {
        return this.type;
    }

    public BankTransaction type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public BankTransaction description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Set<BankTransaction> getLefts() {
        return this.lefts;
    }

    public void setLefts(Set<BankTransaction> bankTransactions) {
        this.lefts = bankTransactions;
    }

    public BankTransaction lefts(Set<BankTransaction> bankTransactions) {
        this.setLefts(bankTransactions);
        return this;
    }

    public BankTransaction addLeft(BankTransaction bankTransaction) {
        this.lefts.add(bankTransaction);
        bankTransaction.getRights().add(this);
        return this;
    }

    public BankTransaction removeLeft(BankTransaction bankTransaction) {
        this.lefts.remove(bankTransaction);
        bankTransaction.getRights().remove(this);
        return this;
    }

    public TransactionBook getTransactionBook() {
        return this.transactionBook;
    }

    public void setTransactionBook(TransactionBook transactionBook) {
        this.transactionBook = transactionBook;
    }

    public BankTransaction transactionBook(TransactionBook transactionBook) {
        this.setTransactionBook(transactionBook);
        return this;
    }

    public Set<BankTransaction> getRights() {
        return this.rights;
    }

    public void setRights(Set<BankTransaction> bankTransactions) {
        if (this.rights != null) {
            this.rights.forEach(i -> i.removeLeft(this));
        }
        if (bankTransactions != null) {
            bankTransactions.forEach(i -> i.addLeft(this));
        }
        this.rights = bankTransactions;
    }

    public BankTransaction rights(Set<BankTransaction> bankTransactions) {
        this.setRights(bankTransactions);
        return this;
    }

    public BankTransaction addRight(BankTransaction bankTransaction) {
        this.rights.add(bankTransaction);
        bankTransaction.getLefts().add(this);
        return this;
    }

    public BankTransaction removeRight(BankTransaction bankTransaction) {
        this.rights.remove(bankTransaction);
        bankTransaction.getLefts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankTransaction)) {
            return false;
        }
        return id != null && id.equals(((BankTransaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

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
}
