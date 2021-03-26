package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
    @JsonIgnoreProperties(value = { "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<Transaction> transactions = new HashSet<>();

    @ManyToMany(mappedBy = "transactionBooks")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenants", "transactionBooks", "apartment" }, allowSetters = true)
    private Set<Lease> leases = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionBook id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public TransactionBook name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionBookType getType() {
        return this.type;
    }

    public TransactionBook type(TransactionBookType type) {
        this.type = type;
        return this;
    }

    public void setType(TransactionBookType type) {
        this.type = type;
    }

    public Set<Transaction> getTransactions() {
        return this.transactions;
    }

    public TransactionBook transactions(Set<Transaction> transactions) {
        this.setTransactions(transactions);
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
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setTransactionBook(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setTransactionBook(this));
        }
        this.transactions = transactions;
    }

    public Set<Lease> getLeases() {
        return this.leases;
    }

    public TransactionBook leases(Set<Lease> leases) {
        this.setLeases(leases);
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
        if (this.leases != null) {
            this.leases.forEach(i -> i.removeTransactionBook(this));
        }
        if (leases != null) {
            leases.forEach(i -> i.addTransactionBook(this));
        }
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
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
