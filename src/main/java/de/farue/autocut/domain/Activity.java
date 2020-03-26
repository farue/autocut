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

import de.farue.autocut.domain.enumeration.SemesterTerms;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "term", nullable = false)
    private SemesterTerms term;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "description")
    private String description;

    @Column(name = "discount")
    private Boolean discount;

    @Column(name = "stw_activity")
    private Boolean stwActivity;

    @OneToMany(mappedBy = "activity")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<TeamMember> teamMembers = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("activities")
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public Activity year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public SemesterTerms getTerm() {
        return term;
    }

    public Activity term(SemesterTerms term) {
        this.term = term;
        return this;
    }

    public void setTerm(SemesterTerms term) {
        this.term = term;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Activity startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Activity endDate(Instant endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public Activity description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isDiscount() {
        return discount;
    }

    public Activity discount(Boolean discount) {
        this.discount = discount;
        return this;
    }

    public void setDiscount(Boolean discount) {
        this.discount = discount;
    }

    public Boolean isStwActivity() {
        return stwActivity;
    }

    public Activity stwActivity(Boolean stwActivity) {
        this.stwActivity = stwActivity;
        return this;
    }

    public void setStwActivity(Boolean stwActivity) {
        this.stwActivity = stwActivity;
    }

    public Set<TeamMember> getTeamMembers() {
        return teamMembers;
    }

    public Activity teamMembers(Set<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
        return this;
    }

    public Activity addTeamMember(TeamMember teamMember) {
        this.teamMembers.add(teamMember);
        teamMember.setActivity(this);
        return this;
    }

    public Activity removeTeamMember(TeamMember teamMember) {
        this.teamMembers.remove(teamMember);
        teamMember.setActivity(null);
        return this;
    }

    public void setTeamMembers(Set<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Activity tenant(Tenant tenant) {
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
        if (!(o instanceof Activity)) {
            return false;
        }
        return id != null && id.equals(((Activity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", year=" + getYear() +
            ", term='" + getTerm() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", discount='" + isDiscount() + "'" +
            ", stwActivity='" + isStwActivity() + "'" +
            "}";
    }
}
