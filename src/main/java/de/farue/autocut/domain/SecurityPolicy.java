package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

import de.farue.autocut.domain.enumeration.ProtectionUnits;

import de.farue.autocut.domain.enumeration.Access;

/**
 * A SecurityPolicy.
 */
@Entity
@Table(name = "security_policy")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
    @JsonIgnoreProperties("securityPolicies")
    private TeamMember teamMember;

    @ManyToOne
    @JsonIgnoreProperties("securityPolicies")
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProtectionUnits getProtectionUnit() {
        return protectionUnit;
    }

    public SecurityPolicy protectionUnit(ProtectionUnits protectionUnit) {
        this.protectionUnit = protectionUnit;
        return this;
    }

    public void setProtectionUnit(ProtectionUnits protectionUnit) {
        this.protectionUnit = protectionUnit;
    }

    public Access getAccess() {
        return access;
    }

    public SecurityPolicy access(Access access) {
        this.access = access;
        return this;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public TeamMember getTeamMember() {
        return teamMember;
    }

    public SecurityPolicy teamMember(TeamMember teamMember) {
        this.teamMember = teamMember;
        return this;
    }

    public void setTeamMember(TeamMember teamMember) {
        this.teamMember = teamMember;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public SecurityPolicy tenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

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
        return 31;
    }

    @Override
    public String toString() {
        return "SecurityPolicy{" +
            "id=" + getId() +
            ", protectionUnit='" + getProtectionUnit() + "'" +
            ", access='" + getAccess() + "'" +
            "}";
    }
}
