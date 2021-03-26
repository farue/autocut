package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.SemesterTerms;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Activity always describes an entire semester since its main purpose is to capture who is\neligible for a discount, and its secondary purpose is to store activity counted at StW.\nBoth these things are in terms of full semesters.
 */
@ApiModel(
    description = "Activity always describes an entire semester since its main purpose is to capture who is\neligible for a discount, and its secondary purpose is to store activity counted at StW.\nBoth these things are in terms of full semesters."
)
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    @Column(name = "start")
    private LocalDate start;

    @Column(name = "end")
    private LocalDate end;

    @Column(name = "description")
    private String description;

    @Column(name = "discount")
    private Boolean discount;

    @Column(name = "stw_activity")
    private Boolean stwActivity;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Tenant tenant;

    @ManyToOne
    @JsonIgnoreProperties(value = { "securityPolicies", "tenant", "team" }, allowSetters = true)
    private TeamMembership teamMembership;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getYear() {
        return this.year;
    }

    public Activity year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public SemesterTerms getTerm() {
        return this.term;
    }

    public Activity term(SemesterTerms term) {
        this.term = term;
        return this;
    }

    public void setTerm(SemesterTerms term) {
        this.term = term;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public Activity start(LocalDate start) {
        this.start = start;
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public Activity end(LocalDate end) {
        this.end = end;
        return this;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public String getDescription() {
        return this.description;
    }

    public Activity description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDiscount() {
        return this.discount;
    }

    public Activity discount(Boolean discount) {
        this.discount = discount;
        return this;
    }

    public void setDiscount(Boolean discount) {
        this.discount = discount;
    }

    public Boolean getStwActivity() {
        return this.stwActivity;
    }

    public Activity stwActivity(Boolean stwActivity) {
        this.stwActivity = stwActivity;
        return this;
    }

    public void setStwActivity(Boolean stwActivity) {
        this.stwActivity = stwActivity;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public Activity tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public TeamMembership getTeamMembership() {
        return this.teamMembership;
    }

    public Activity teamMembership(TeamMembership teamMembership) {
        this.setTeamMembership(teamMembership);
        return this;
    }

    public void setTeamMembership(TeamMembership teamMembership) {
        this.teamMembership = teamMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", year=" + getYear() +
            ", term='" + getTerm() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            ", description='" + getDescription() + "'" +
            ", discount='" + getDiscount() + "'" +
            ", stwActivity='" + getStwActivity() + "'" +
            "}";
    }
}
