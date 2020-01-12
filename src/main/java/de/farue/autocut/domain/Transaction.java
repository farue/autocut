package de.farue.autocut.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import de.farue.autocut.domain.enumeration.TransactionKind;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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

    @Column(name = "details")
    private String details;

    @NotNull
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "recipient")
    private String recipient;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "balance", precision = 21, scale = 2, nullable = false)
    private BigDecimal balance;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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

    public String getDetails() {
        return details;
    }

    public Transaction details(String details) {
        this.details = details;
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Transaction balance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

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

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", kind='" + getKind() + "'" +
            ", bookingDate='" + getBookingDate() + "'" +
            ", valueDate='" + getValueDate() + "'" +
            ", details='" + getDetails() + "'" +
            ", issuer='" + getIssuer() + "'" +
            ", recipient='" + getRecipient() + "'" +
            ", amount=" + getAmount() +
            ", balance=" + getBalance() +
            "}";
    }
}
