package de.farue.autocut.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Tenant.
 */
@Entity
@Table(name = "tenant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TeamMember> teamMemberships = new HashSet<>();

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<SecurityPolicy> securityPolicies = new HashSet<>();

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Activity> activties = new HashSet<>();

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TenantCommunication> messages = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("tenants")
    private Lease lease;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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

    public String getEmail() {
        return email;
    }

    public Tenant email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Tenant createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Tenant createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Tenant lastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Tenant lastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

    public Set<TeamMember> getTeamMemberships() {
        return teamMemberships;
    }

    public Tenant teamMemberships(Set<TeamMember> teamMembers) {
        this.teamMemberships = teamMembers;
        return this;
    }

    public Tenant addTeamMemberships(TeamMember teamMember) {
        this.teamMemberships.add(teamMember);
        teamMember.setTenant(this);
        return this;
    }

    public Tenant removeTeamMemberships(TeamMember teamMember) {
        this.teamMemberships.remove(teamMember);
        teamMember.setTenant(null);
        return this;
    }

    public void setTeamMemberships(Set<TeamMember> teamMembers) {
        this.teamMemberships = teamMembers;
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

    public Set<Activity> getActivties() {
        return activties;
    }

    public Tenant activties(Set<Activity> activities) {
        this.activties = activities;
        return this;
    }

    public Tenant addActivties(Activity activity) {
        this.activties.add(activity);
        activity.setTenant(this);
        return this;
    }

    public Tenant removeActivties(Activity activity) {
        this.activties.remove(activity);
        activity.setTenant(null);
        return this;
    }

    public void setActivties(Set<Activity> activities) {
        this.activties = activities;
    }

    public Set<TenantCommunication> getMessages() {
        return messages;
    }

    public Tenant messages(Set<TenantCommunication> tenantCommunications) {
        this.messages = tenantCommunications;
        return this;
    }

    public Tenant addMessages(TenantCommunication tenantCommunication) {
        this.messages.add(tenantCommunication);
        tenantCommunication.setTenant(this);
        return this;
    }

    public Tenant removeMessages(TenantCommunication tenantCommunication) {
        this.messages.remove(tenantCommunication);
        tenantCommunication.setTenant(null);
        return this;
    }

    public void setMessages(Set<TenantCommunication> tenantCommunications) {
        this.messages = tenantCommunications;
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
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

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

    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
