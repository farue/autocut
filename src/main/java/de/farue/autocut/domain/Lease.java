package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Lease.
 */
@Entity
@Table(name = "lease")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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

    @Column(name = "end")
    private Instant end;

    @Column(name = "blocked")
    private Boolean blocked;

    @Lob
    @Column(name = "picture_contract")
    private byte[] pictureContract;

    @Column(name = "picture_contract_content_type")
    private String pictureContractContentType;

    @OneToMany(mappedBy = "lease")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Tenant> tenants = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("leases")
    private Apartment apartment;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

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
