package de.farue.autocut.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Tenant.
 */
@Entity
@Table(name = "tenant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Lob
    @Column(name = "picture_id")
    private byte[] pictureId;

    @Column(name = "picture_id_content_type")
    private String pictureIdContentType;

    @Column(name = "verified")
    private Boolean verified;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<SecurityPolicy> securityPolicies = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "tenants", allowSetters = true)
    private Lease lease;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public Tenant firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Tenant lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getPictureId() {
        return pictureId;
    }

    public Tenant pictureId(byte[] pictureId) {
        this.pictureId = pictureId;
        return this;
    }

    public void setPictureId(byte[] pictureId) {
        this.pictureId = pictureId;
    }

    public String getPictureIdContentType() {
        return pictureIdContentType;
    }

    public Tenant pictureIdContentType(String pictureIdContentType) {
        this.pictureIdContentType = pictureIdContentType;
        return this;
    }

    public void setPictureIdContentType(String pictureIdContentType) {
        this.pictureIdContentType = pictureIdContentType;
    }

    public Boolean isVerified() {
        return verified;
    }

    public Tenant verified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public User getUser() {
        return user;
    }

    public Tenant user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<SecurityPolicy> getSecurityPolicies() {
        return securityPolicies;
    }

    public Tenant securityPolicies(Set<SecurityPolicy> securityPolicies) {
        this.securityPolicies = securityPolicies;
        return this;
    }

    public Tenant addSecurityPolicies(SecurityPolicy securityPolicy) {
        this.securityPolicies.add(securityPolicy);
        securityPolicy.setTenant(this);
        return this;
    }

    public Tenant removeSecurityPolicies(SecurityPolicy securityPolicy) {
        this.securityPolicies.remove(securityPolicy);
        securityPolicy.setTenant(null);
        return this;
    }

    public void setSecurityPolicies(Set<SecurityPolicy> securityPolicies) {
        this.securityPolicies = securityPolicies;
    }

    public Lease getLease() {
        return lease;
    }

    public Tenant lease(Lease lease) {
        this.lease = lease;
        return this;
    }

    public void setLease(Lease lease) {
        this.lease = lease;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenant)) {
            return false;
        }
        return id != null && id.equals(((Tenant) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", pictureId='" + getPictureId() + "'" +
            ", pictureIdContentType='" + getPictureIdContentType() + "'" +
            ", verified='" + isVerified() + "'" +
            "}";
    }
}
