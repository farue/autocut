package de.farue.autocut.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Lease.
 */
@Entity
@Table(name = "lease")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Lease implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nr", nullable = false)
    private String nr;

    @NotNull
    @Column(name = "start", nullable = false)
    private Instant start;

    @NotNull
    @Column(name = "end", nullable = false)
    private Instant end;

    @Column(name = "blocked")
    private Boolean blocked;

    @Lob
    @Column(name = "picture_contract")
    private byte[] pictureContract;

    @Column(name = "picture_contract_content_type")
    private String pictureContractContentType;

    @OneToMany(mappedBy = "lease")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Tenant> tenants = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "lease_transaction_book",
               joinColumns = @JoinColumn(name = "lease_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "transaction_book_id", referencedColumnName = "id"))
    private Set<TransactionBook> transactionBooks = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "leases", allowSetters = true)
    private Apartment apartment;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNr() {
        return nr;
    }

    public Lease nr(String nr) {
        this.nr = nr;
        return this;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public Instant getStart() {
        return start;
    }

    public Lease start(Instant start) {
        this.start = start;
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public Lease end(Instant end) {
        this.end = end;
        return this;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public Lease blocked(Boolean blocked) {
        this.blocked = blocked;
        return this;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public byte[] getPictureContract() {
        return pictureContract;
    }

    public Lease pictureContract(byte[] pictureContract) {
        this.pictureContract = pictureContract;
        return this;
    }

    public void setPictureContract(byte[] pictureContract) {
        this.pictureContract = pictureContract;
    }

    public String getPictureContractContentType() {
        return pictureContractContentType;
    }

    public Lease pictureContractContentType(String pictureContractContentType) {
        this.pictureContractContentType = pictureContractContentType;
        return this;
    }

    public void setPictureContractContentType(String pictureContractContentType) {
        this.pictureContractContentType = pictureContractContentType;
    }

    public Set<Tenant> getTenants() {
        return tenants;
    }

    public Lease tenants(Set<Tenant> tenants) {
        this.tenants = tenants;
        return this;
    }

    public Lease addTenants(Tenant tenant) {
        this.tenants.add(tenant);
        tenant.setLease(this);
        return this;
    }

    public Lease removeTenants(Tenant tenant) {
        this.tenants.remove(tenant);
        tenant.setLease(null);
        return this;
    }

    public void setTenants(Set<Tenant> tenants) {
        this.tenants = tenants;
    }

    public Set<TransactionBook> getTransactionBooks() {
        return transactionBooks;
    }

    public Lease transactionBooks(Set<TransactionBook> transactionBooks) {
        this.transactionBooks = transactionBooks;
        return this;
    }

    public Lease addTransactionBook(TransactionBook transactionBook) {
        this.transactionBooks.add(transactionBook);
        return this;
    }

    public Lease removeTransactionBook(TransactionBook transactionBook) {
        this.transactionBooks.remove(transactionBook);
        return this;
    }

    public void setTransactionBooks(Set<TransactionBook> transactionBooks) {
        this.transactionBooks = transactionBooks;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public Lease apartment(Apartment apartment) {
        this.apartment = apartment;
        return this;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lease)) {
            return false;
        }
        return id != null && id.equals(((Lease) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lease{" +
            "id=" + getId() +
            ", nr='" + getNr() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            ", blocked='" + isBlocked() + "'" +
            ", pictureContract='" + getPictureContract() + "'" +
            ", pictureContractContentType='" + getPictureContractContentType() + "'" +
            "}";
    }
}
