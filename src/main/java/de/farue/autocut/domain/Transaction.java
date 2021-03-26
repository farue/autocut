package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.TransactionKind;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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

    @NotNull
    @Column(name = "balance_after", precision = 21, scale = 2, nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "description")
    private String description;

    @Column(name = "service_qulifier")
    private String serviceQulifier;

    @NotNull
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "recipient")
    private String recipient;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_transaction__left",
        joinColumns = @JoinColumn(name = "transaction_id"),
        inverseJoinColumns = @JoinColumn(name = "left_id")
    )
    @JsonIgnoreProperties(value = { "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<Transaction> lefts = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "transactions", "leases" }, allowSetters = true)
    private TransactionBook transactionBook;

    @ManyToMany(mappedBy = "lefts")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<Transaction> rights = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction id(Long id) {
        this.id = id;
        return this;
    }

    public TransactionKind getKind() {
        return this.kind;
    }

    public Transaction kind(TransactionKind kind) {
        this.kind = kind;
        return this;
    }

    public void setKind(TransactionKind kind) {
        this.kind = kind;
    }

    public Instant getBookingDate() {
        return this.bookingDate;
    }

    public Transaction bookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
        return this;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Instant getValueDate() {
        return this.valueDate;
    }

    public Transaction valueDate(Instant valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public void setValueDate(Instant valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public Transaction value(BigDecimal value) {
        this.value = value;
        return this;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getBalanceAfter() {
        return this.balanceAfter;
    }

    public Transaction balanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
        return this;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return this.description;
    }

    public Transaction description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceQulifier() {
        return this.serviceQulifier;
    }

    public Transaction serviceQulifier(String serviceQulifier) {
        this.serviceQulifier = serviceQulifier;
        return this;
    }

    public void setServiceQulifier(String serviceQulifier) {
        this.serviceQulifier = serviceQulifier;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public Transaction issuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public Transaction recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Set<Transaction> getLefts() {
        return this.lefts;
    }

    public Transaction lefts(Set<Transaction> transactions) {
        this.setLefts(transactions);
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

    public TransactionBook getTransactionBook() {
        return this.transactionBook;
    }

    public Transaction transactionBook(TransactionBook transactionBook) {
        this.setTransactionBook(transactionBook);
        return this;
    }

    public void setTransactionBook(TransactionBook transactionBook) {
        this.transactionBook = transactionBook;
    }

    public Set<Transaction> getRights() {
        return this.rights;
    }

    public Transaction rights(Set<Transaction> transactions) {
        this.setRights(transactions);
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
        if (this.rights != null) {
            this.rights.forEach(i -> i.removeLeft(this));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.addLeft(this));
        }
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
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
            ", serviceQulifier='" + getServiceQulifier() + "'" +
            ", issuer='" + getIssuer() + "'" +
            ", recipient='" + getRecipient() + "'" +
            "}";
    }
}
