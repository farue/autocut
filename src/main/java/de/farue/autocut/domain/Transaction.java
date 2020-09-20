package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import de.farue.autocut.domain.enumeration.TransactionKind;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false)
    private TransactionKind kind;

    @NotNull
    @Column(name = "booking_date", nullable = false)
    private Instant bookingDate;

    @NotNull
    @Column(name = "value_date", nullable = false)
    private Instant valueDate;

    @NotNull
    @Column(name = "value", precision = 21, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "balance_after", precision = 21, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "recipient")
    private String recipient;

    @ManyToOne
    @JsonIgnoreProperties(value = "transactions", allowSetters = true)
    private TransactionBook account;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "transaction_left",
               joinColumns = @JoinColumn(name = "transaction_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "left_id", referencedColumnName = "id"))
    private Set<Transaction> lefts = new HashSet<>();

    @ManyToMany(mappedBy = "lefts")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Transaction> rights = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionKind getKind() {
        return kind;
    }

    public Transaction kind(TransactionKind kind) {
        this.kind = kind;
        return this;
    }

    public void setKind(TransactionKind kind) {
        this.kind = kind;
    }

    public Instant getBookingDate() {
        return bookingDate;
    }

    public Transaction bookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
        return this;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Instant getValueDate() {
        return valueDate;
    }

    public Transaction valueDate(Instant valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public void setValueDate(Instant valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Transaction value(BigDecimal value) {
        this.value = value;
        return this;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public Transaction balanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
        return this;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public Transaction description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssuer() {
        return issuer;
    }

    public Transaction issuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRecipient() {
        return recipient;
    }

    public Transaction recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public TransactionBook getAccount() {
        return account;
    }

    public Transaction account(TransactionBook transactionBook) {
        this.account = transactionBook;
        return this;
    }

    public void setAccount(TransactionBook transactionBook) {
        this.account = transactionBook;
    }

    public Set<Transaction> getLefts() {
        return lefts;
    }

    public Transaction lefts(Set<Transaction> transactions) {
        this.lefts = transactions;
        return this;
    }

    public Transaction addLeft(Transaction transaction) {
        this.lefts.add(transaction);
        transaction.getRights().add(this);
        return this;
    }

    public Transaction removeLeft(Transaction transaction) {
        this.lefts.remove(transaction);
        transaction.getRights().remove(this);
        return this;
    }

    public void setLefts(Set<Transaction> transactions) {
        this.lefts = transactions;
    }

    public Set<Transaction> getRights() {
        return rights;
    }

    public Transaction rights(Set<Transaction> transactions) {
        this.rights = transactions;
        return this;
    }

    public Transaction addRight(Transaction transaction) {
        this.rights.add(transaction);
        transaction.getLefts().add(this);
        return this;
    }

    public Transaction removeRight(Transaction transaction) {
        this.rights.remove(transaction);
        transaction.getLefts().remove(this);
        return this;
    }

    public void setRights(Set<Transaction> transactions) {
        this.rights = transactions;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", kind='" + getKind() + "'" +
            ", bookingDate='" + getBookingDate() + "'" +
            ", valueDate='" + getValueDate() + "'" +
            ", value=" + getValue() +
            ", balanceAfter=" + getBalanceAfter() +
            ", description='" + getDescription() + "'" +
            ", issuer='" + getIssuer() + "'" +
            ", recipient='" + getRecipient() + "'" +
            "}";
    }
}
