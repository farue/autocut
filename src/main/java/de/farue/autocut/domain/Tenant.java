package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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

    @OneToOne(fetch = FetchType.LAZY)

    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TeamMember> teamMemberships = new HashSet<>();

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TenantCommunication> messages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
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
            "}";
    }
}
