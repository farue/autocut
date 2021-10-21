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
 * A Team.
 */
@Entity
@Table(name = "team")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "team")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "securityPolicies", "tenant", "team" }, allowSetters = true)
    private Set<TeamMembership> teamMemberships = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Team id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Team name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TeamMembership> getTeamMemberships() {
        return this.teamMemberships;
    }

    public void setTeamMemberships(Set<TeamMembership> teamMemberships) {
        if (this.teamMemberships != null) {
            this.teamMemberships.forEach(i -> i.setTeam(null));
        }
        if (teamMemberships != null) {
            teamMemberships.forEach(i -> i.setTeam(this));
        }
        this.teamMemberships = teamMemberships;
    }

    public Team teamMemberships(Set<TeamMembership> teamMemberships) {
        this.setTeamMemberships(teamMemberships);
        return this;
    }

    public Team addTeamMembership(TeamMembership teamMembership) {
        this.teamMemberships.add(teamMembership);
        teamMembership.setTeam(this);
        return this;
    }

    public Team removeTeamMembership(TeamMembership teamMembership) {
        this.teamMemberships.remove(teamMembership);
        teamMembership.setTeam(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        return id != null && id.equals(((Team) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Team{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
