package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nr", nullable = false)
    private String nr;

    @NotNull
    @Column(name = "start", nullable = false)
    private LocalDate start;

    @NotNull
    @Column(name = "end", nullable = false)
    private LocalDate end;

    @Column(name = "blocked")
    private Boolean blocked;

    @Lob
    @Column(name = "picture_contract")
    private byte[] pictureContract;

    @Column(name = "picture_contract_content_type")
    private String pictureContractContentType;

    @OneToMany(mappedBy = "lease")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Set<Tenant> tenants = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_lease__transaction_book",
        joinColumns = @JoinColumn(name = "lease_id"),
        inverseJoinColumns = @JoinColumn(name = "transaction_book_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "iTransactions", "bTransactions", "leases" }, allowSetters = true)
    private Set<TransactionBook> transactionBooks = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "internetAccess", "leases", "address" }, allowSetters = true)
    private Apartment apartment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lease id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNr() {
        return this.nr;
    }

    public Lease nr(String nr) {
        this.setNr(nr);
        return this;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public Lease start(LocalDate start) {
        this.setStart(start);
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public Lease end(LocalDate end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Boolean getBlocked() {
        return this.blocked;
    }

    public Lease blocked(Boolean blocked) {
        this.setBlocked(blocked);
        return this;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public byte[] getPictureContract() {
        return this.pictureContract;
    }

    public Lease pictureContract(byte[] pictureContract) {
        this.setPictureContract(pictureContract);
        return this;
    }

    public void setPictureContract(byte[] pictureContract) {
        this.pictureContract = pictureContract;
    }

    public String getPictureContractContentType() {
        return this.pictureContractContentType;
    }

    public Lease pictureContractContentType(String pictureContractContentType) {
        this.pictureContractContentType = pictureContractContentType;
        return this;
    }

    public void setPictureContractContentType(String pictureContractContentType) {
        this.pictureContractContentType = pictureContractContentType;
    }

    public Set<Tenant> getTenants() {
        return this.tenants;
    }

    public void setTenants(Set<Tenant> tenants) {
        if (this.tenants != null) {
            this.tenants.forEach(i -> i.setLease(null));
        }
        if (tenants != null) {
            tenants.forEach(i -> i.setLease(this));
        }
        this.tenants = tenants;
    }

    public Lease tenants(Set<Tenant> tenants) {
        this.setTenants(tenants);
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

    public Set<TransactionBook> getTransactionBooks() {
        return this.transactionBooks;
    }

    public void setTransactionBooks(Set<TransactionBook> transactionBooks) {
        this.transactionBooks = transactionBooks;
    }

    public Lease transactionBooks(Set<TransactionBook> transactionBooks) {
        this.setTransactionBooks(transactionBooks);
        return this;
    }

    public Lease addTransactionBook(TransactionBook transactionBook) {
        this.transactionBooks.add(transactionBook);
        transactionBook.getLeases().add(this);
        return this;
    }

    public Lease removeTransactionBook(TransactionBook transactionBook) {
        this.transactionBooks.remove(transactionBook);
        transactionBook.getLeases().remove(this);
        return this;
    }

    public Apartment getApartment() {
        return this.apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Lease apartment(Apartment apartment) {
        this.setApartment(apartment);
        return this;
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lease{" +
            "id=" + getId() +
            ", nr='" + getNr() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            ", blocked='" + getBlocked() + "'" +
            ", pictureContract='" + getPictureContract() + "'" +
            ", pictureContractContentType='" + getPictureContractContentType() + "'" +
            "}";
    }
}
