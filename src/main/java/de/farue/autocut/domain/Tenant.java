package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
    @Column(name = "id")
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
    @JsonIgnoreProperties(value = { "teamMember", "tenant" }, allowSetters = true)
    private Set<SecurityPolicy> securityPolicies = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "tenants", "transactionBooks" }, allowSetters = true)
    private Lease lease;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tenant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Tenant firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Tenant lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getPictureId() {
        return this.pictureId;
    }

    public Tenant pictureId(byte[] pictureId) {
        this.setPictureId(pictureId);
        return this;
    }

    public void setPictureId(byte[] pictureId) {
        this.pictureId = pictureId;
    }

    public String getPictureIdContentType() {
        return this.pictureIdContentType;
    }

    public Tenant pictureIdContentType(String pictureIdContentType) {
        this.pictureIdContentType = pictureIdContentType;
        return this;
    }

    public void setPictureIdContentType(String pictureIdContentType) {
        this.pictureIdContentType = pictureIdContentType;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public Tenant verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tenant user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<SecurityPolicy> getSecurityPolicies() {
        return this.securityPolicies;
    }

    public void setSecurityPolicies(Set<SecurityPolicy> securityPolicies) {
        if (this.securityPolicies != null) {
            this.securityPolicies.forEach(i -> i.setTenant(null));
        }
        if (securityPolicies != null) {
            securityPolicies.forEach(i -> i.setTenant(this));
        }
        this.securityPolicies = securityPolicies;
    }

    public Tenant securityPolicies(Set<SecurityPolicy> securityPolicies) {
        this.setSecurityPolicies(securityPolicies);
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

    public Lease getLease() {
        return this.lease;
    }

    public void setLease(Lease lease) {
        this.lease = lease;
    }

    public Tenant lease(Lease lease) {
        this.setLease(lease);
        return this;
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
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
            ", verified='" + getVerified() + "'" +
            "}";
    }
}
