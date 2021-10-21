package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.TeamRole;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TeamMembership.
 */
@Entity
@Table(name = "team_membership")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TeamMembership implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private TeamRole role;

    @Column(name = "start")
    private LocalDate start;

    @Column(name = "end")
    private LocalDate end;

    @OneToMany(mappedBy = "teamMember")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "teamMember", "tenant" }, allowSetters = true)
    private Set<SecurityPolicy> securityPolicies = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Tenant tenant;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "teamMemberships" }, allowSetters = true)
    private Team team;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeamMembership id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamRole getRole() {
        return this.role;
    }

    public TeamMembership role(TeamRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public TeamMembership start(LocalDate start) {
        this.setStart(start);
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public TeamMembership end(LocalDate end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Set<SecurityPolicy> getSecurityPolicies() {
        return this.securityPolicies;
    }

    public void setSecurityPolicies(Set<SecurityPolicy> securityPolicies) {
        if (this.securityPolicies != null) {
            this.securityPolicies.forEach(i -> i.setTeamMember(null));
        }
        if (securityPolicies != null) {
            securityPolicies.forEach(i -> i.setTeamMember(this));
        }
        this.securityPolicies = securityPolicies;
    }

    public TeamMembership securityPolicies(Set<SecurityPolicy> securityPolicies) {
        this.setSecurityPolicies(securityPolicies);
        return this;
    }

    public TeamMembership addSecurityPolicies(SecurityPolicy securityPolicy) {
        this.securityPolicies.add(securityPolicy);
        securityPolicy.setTeamMember(this);
        return this;
    }

    public TeamMembership removeSecurityPolicies(SecurityPolicy securityPolicy) {
        this.securityPolicies.remove(securityPolicy);
        securityPolicy.setTeamMember(null);
        return this;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public TeamMembership tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TeamMembership team(Team team) {
        this.setTeam(team);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamMembership)) {
            return false;
        }
        return id != null && id.equals(((TeamMembership) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamMembership{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            "}";
    }
}
