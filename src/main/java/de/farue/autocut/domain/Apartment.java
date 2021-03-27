package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.farue.autocut.domain.enumeration.ApartmentTypes;

/**
 * A Apartment.
 */
@Entity
@Table(name = "apartment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Apartment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nr", nullable = false)
    private String nr;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ApartmentTypes type;

    @NotNull
    @Min(value = 0)
    @Column(name = "max_number_of_leases", nullable = false)
    private Integer maxNumberOfLeases;

    @JsonIgnoreProperties(value = { "networkSwitch", "apartment" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private InternetAccess internetAccess;

    @OneToMany(mappedBy = "apartment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenants", "transactionBooks", "apartment" }, allowSetters = true)
    private Set<Lease> leases = new HashSet<>();

    @ManyToOne
    private Address address;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Apartment id(Long id) {
        this.id = id;
        return this;
    }

    public String getNr() {
        return this.nr;
    }

    public Apartment nr(String nr) {
        this.nr = nr;
        return this;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public ApartmentTypes getType() {
        return this.type;
    }

    public Apartment type(ApartmentTypes type) {
        this.type = type;
        return this;
    }

    public void setType(ApartmentTypes type) {
        this.type = type;
    }

    public Integer getMaxNumberOfLeases() {
        return this.maxNumberOfLeases;
    }

    public Apartment maxNumberOfLeases(Integer maxNumberOfLeases) {
        this.maxNumberOfLeases = maxNumberOfLeases;
        return this;
    }

    public void setMaxNumberOfLeases(Integer maxNumberOfLeases) {
        this.maxNumberOfLeases = maxNumberOfLeases;
    }

    public InternetAccess getInternetAccess() {
        return this.internetAccess;
    }

    public Apartment internetAccess(InternetAccess internetAccess) {
        this.setInternetAccess(internetAccess);
        return this;
    }

    public void setInternetAccess(InternetAccess internetAccess) {
        this.internetAccess = internetAccess;
    }

    public Set<Lease> getLeases() {
        return this.leases;
    }

    public Apartment leases(Set<Lease> leases) {
        this.setLeases(leases);
        return this;
    }

    public Apartment addLeases(Lease lease) {
        this.leases.add(lease);
        lease.setApartment(this);
        return this;
    }

    public Apartment removeLeases(Lease lease) {
        this.leases.remove(lease);
        lease.setApartment(null);
        return this;
    }

    public void setLeases(Set<Lease> leases) {
        if (this.leases != null) {
            this.leases.forEach(i -> i.setApartment(null));
        }
        if (leases != null) {
            leases.forEach(i -> i.setApartment(this));
        }
        this.leases = leases;
    }

    public Address getAddress() {
        return this.address;
    }

    public Apartment address(Address address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Apartment)) {
            return false;
        }
        return id != null && id.equals(((Apartment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Apartment{" +
            "id=" + getId() +
            ", nr='" + getNr() + "'" +
            ", type='" + getType() + "'" +
            ", maxNumberOfLeases=" + getMaxNumberOfLeases() +
            "}";
    }
}
