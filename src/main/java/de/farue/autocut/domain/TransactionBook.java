package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.farue.autocut.domain.enumeration.TransactionBookType;

/**
 * A TransactionBook.
 */
@Entity
@Table(name = "transaction_book")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TransactionBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionBookType type;

    @OneToMany(mappedBy = "transactionBook")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Transaction> transactions = new HashSet<>();

    @ManyToMany(mappedBy = "transactionBooks")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Lease> leases = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public TransactionBook name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionBookType getType() {
        return type;
    }

    public TransactionBook type(TransactionBookType type) {
        this.type = type;
        return this;
    }

    public void setType(TransactionBookType type) {
        this.type = type;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public TransactionBook transactions(Set<Transaction> transactions) {
        this.transactions = transactions;
        return this;
    }

    public TransactionBook addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setTransactionBook(this);
        return this;
    }

    public TransactionBook removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setTransactionBook(null);
        return this;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Lease> getLeases() {
        return leases;
    }

    public TransactionBook leases(Set<Lease> leases) {
        this.leases = leases;
        return this;
    }

    public TransactionBook addLease(Lease lease) {
        this.leases.add(lease);
        lease.getTransactionBooks().add(this);
        return this;
    }

    public TransactionBook removeLease(Lease lease) {
        this.leases.remove(lease);
        lease.getTransactionBooks().remove(this);
        return this;
    }

    public void setLeases(Set<Lease> leases) {
        this.leases = leases;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionBook)) {
            return false;
        }
        return id != null && id.equals(((TransactionBook) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionBook{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
