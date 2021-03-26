package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.Access;
import de.farue.autocut.domain.enumeration.ProtectionUnits;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SecurityPolicy.
 */
@Entity
@Table(name = "security_policy")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SecurityPolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "protection_unit", nullable = false)
    private ProtectionUnits protectionUnit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "access", nullable = false)
    private Access access;

    @ManyToOne
    @JsonIgnoreProperties(value = { "securityPolicies", "tenant", "team" }, allowSetters = true)
    private TeamMembership teamMember;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SecurityPolicy id(Long id) {
        this.id = id;
        return this;
    }

    public ProtectionUnits getProtectionUnit() {
        return this.protectionUnit;
    }

    public SecurityPolicy protectionUnit(ProtectionUnits protectionUnit) {
        this.protectionUnit = protectionUnit;
        return this;
    }

    public void setProtectionUnit(ProtectionUnits protectionUnit) {
        this.protectionUnit = protectionUnit;
    }

    public Access getAccess() {
        return this.access;
    }

    public SecurityPolicy access(Access access) {
        this.access = access;
        return this;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public TeamMembership getTeamMember() {
        return this.teamMember;
    }

    public SecurityPolicy teamMember(TeamMembership teamMembership) {
        this.setTeamMember(teamMembership);
        return this;
    }

    public void setTeamMember(TeamMembership teamMembership) {
        this.teamMember = teamMembership;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public SecurityPolicy tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityPolicy)) {
            return false;
        }
        return id != null && id.equals(((SecurityPolicy) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityPolicy{" +
            "id=" + getId() +
            ", protectionUnit='" + getProtectionUnit() + "'" +
            ", access='" + getAccess() + "'" +
            "}";
    }
}
