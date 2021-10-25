package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.TransactionBookType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @Column(name = "id")
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
    private Set<InternalTransaction> iTransactions = new HashSet<>();

    @OneToMany(mappedBy = "transactionBook")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "bankAccount", "contraBankAccount", "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<BankTransaction> bTransactions = new HashSet<>();

    @ManyToMany(mappedBy = "transactionBooks")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenants", "transactionBooks", "apartment" }, allowSetters = true)
    private Set<Lease> leases = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionBook id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TransactionBook name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionBookType getType() {
        return this.type;
    }

    public TransactionBook type(TransactionBookType type) {
        this.setType(type);
        return this;
    }

    public void setType(TransactionBookType type) {
        this.type = type;
    }

    public Set<InternalTransaction> getITransactions() {
        return this.iTransactions;
    }

    public void setITransactions(Set<InternalTransaction> internalTransactions) {
        if (this.iTransactions != null) {
            this.iTransactions.forEach(i -> i.setTransactionBook(null));
        }
        if (internalTransactions != null) {
            internalTransactions.forEach(i -> i.setTransactionBook(this));
        }
        this.iTransactions = internalTransactions;
    }

    public TransactionBook iTransactions(Set<InternalTransaction> internalTransactions) {
        this.setITransactions(internalTransactions);
        return this;
    }

    public TransactionBook addITransaction(InternalTransaction internalTransaction) {
        this.iTransactions.add(internalTransaction);
        internalTransaction.setTransactionBook(this);
        return this;
    }

    public TransactionBook removeITransaction(InternalTransaction internalTransaction) {
        this.iTransactions.remove(internalTransaction);
        internalTransaction.setTransactionBook(null);
        return this;
    }

    public Set<BankTransaction> getBTransactions() {
        return this.bTransactions;
    }

    public void setBTransactions(Set<BankTransaction> bankTransactions) {
        if (this.bTransactions != null) {
            this.bTransactions.forEach(i -> i.setTransactionBook(null));
        }
        if (bankTransactions != null) {
            bankTransactions.forEach(i -> i.setTransactionBook(this));
        }
        this.bTransactions = bankTransactions;
    }

    public TransactionBook bTransactions(Set<BankTransaction> bankTransactions) {
        this.setBTransactions(bankTransactions);
        return this;
    }

    public TransactionBook addBTransaction(BankTransaction bankTransaction) {
        this.bTransactions.add(bankTransaction);
        bankTransaction.setTransactionBook(this);
        return this;
    }

    public TransactionBook removeBTransaction(BankTransaction bankTransaction) {
        this.bTransactions.remove(bankTransaction);
        bankTransaction.setTransactionBook(null);
        return this;
    }

    public Set<Lease> getLeases() {
        return this.leases;
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
