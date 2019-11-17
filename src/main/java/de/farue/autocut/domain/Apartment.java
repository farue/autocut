package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Apartment.
 */
@Entity
@Table(name = "apartment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Apartment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "apartment_nr", nullable = false)
    private String apartmentNr;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "apartment_type", nullable = false)
    private ApartmentTypes apartmentType;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull

    @JoinColumn(unique = true)
    private InternetAccess internetAccess;

    @OneToMany(mappedBy = "apartment")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Lease> leases = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("apartments")
    private Address address;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApartmentNr() {
        return apartmentNr;
    }

    public Apartment apartmentNr(String apartmentNr) {
        this.apartmentNr = apartmentNr;
        return this;
    }

    public void setApartmentNr(String apartmentNr) {
        this.apartmentNr = apartmentNr;
    }

    public ApartmentTypes getApartmentType() {
        return apartmentType;
    }

    public Apartment apartmentType(ApartmentTypes apartmentType) {
        this.apartmentType = apartmentType;
        return this;
    }

    public void setApartmentType(ApartmentTypes apartmentType) {
        this.apartmentType = apartmentType;
    }

    public InternetAccess getInternetAccess() {
        return internetAccess;
    }

    public Apartment internetAccess(InternetAccess internetAccess) {
        this.internetAccess = internetAccess;
        return this;
    }

    public void setInternetAccess(InternetAccess internetAccess) {
        this.internetAccess = internetAccess;
    }

    public Set<Lease> getLeases() {
        return leases;
    }

    public Apartment leases(Set<Lease> leases) {
        this.leases = leases;
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
        this.leases = leases;
    }

    public Address getAddress() {
        return address;
    }

    public Apartment address(Address address) {
        this.address = address;
        return this;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

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
        return 31;
    }

    @Override
    public String toString() {
        return "Apartment{" +
            "id=" + getId() +
            ", apartmentNr='" + getApartmentNr() + "'" +
            ", apartmentType='" + getApartmentType() + "'" +
            "}";
    }
}
