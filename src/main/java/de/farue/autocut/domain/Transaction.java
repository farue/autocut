package de.farue.autocut.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

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

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "transaction_link",
               joinColumns = @JoinColumn(name = "right_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "left_id", referencedColumnName = "id"))
    @JsonIgnoreProperties({"lefts"})
    private Set<Transaction> lefts = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "transactions", allowSetters = true)
    private TransactionBook transactionBook;

    @ManyToMany(mappedBy = "lefts")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Transaction> rights = new HashSet<>();

    @PreRemove
    private void removeLinks() {
        for (Transaction linkedTransaction : lefts) {
            linkedTransaction.removeLeft(this);
        }
        for (Transaction linkedTransaction : rights) {
            linkedTransaction.removeRight(this);
        }
        lefts.clear();
        rights.clear();
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Transaction type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getBookingDate() {
        return bookingDate;
    }

    public Transaction bookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate.truncatedTo(ChronoUnit.MILLIS);
        return this;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Instant getValueDate() {
        return valueDate;
    }

    public Transaction valueDate(Instant valueDate) {
        this.valueDate = valueDate.truncatedTo(ChronoUnit.MILLIS);
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

    public String getServiceQulifier() {
        return serviceQulifier;
    }

    public Transaction serviceQulifier(String serviceQulifier) {
        this.serviceQulifier = serviceQulifier;
        return this;
    }

    public void setServiceQulifier(String serviceQulifier) {
        this.serviceQulifier = serviceQulifier;
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

    public TransactionBook getTransactionBook() {
        return transactionBook;
    }

    public Transaction transactionBook(TransactionBook transactionBook) {
        this.transactionBook = transactionBook;
        return this;
    }

    public void setTransactionBook(TransactionBook transactionBook) {
        this.transactionBook = transactionBook;
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

    public void link(Transaction transaction) {
        addLeft(transaction);
        addRight(transaction);
    }

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
            ", type='" + getType() + "'" +
            ", bookingDate='" + getBookingDate() + "'" +
            ", valueDate='" + getValueDate() + "'" +
            ", value=" + getValue() +
            ", balanceAfter=" + getBalanceAfter() +
            ", description='" + getDescription() + "'" +
            ", serviceQulifier='" + getServiceQulifier() + "'" +
            "}";
    }
}
