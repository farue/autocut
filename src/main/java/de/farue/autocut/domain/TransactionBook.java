package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
            "}";
    }
}
