package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimesheetTask.
 */
@Entity
@Table(name = "timesheet_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TimesheetTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @NotNull
    @Column(name = "constant", nullable = false)
    private Integer constant;

    @NotNull
    @Column(name = "constant_editable", nullable = false)
    private Boolean constantEditable;

    @NotNull
    @Column(name = "factor", precision = 21, scale = 2, nullable = false)
    private BigDecimal factor;

    @NotNull
    @Column(name = "factor_editable", nullable = false)
    private Boolean factorEditable;

    @Column(name = "default_timespan")
    private Integer defaultTimespan;

    @ManyToMany(mappedBy = "tasks")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "owner", "tasks" }, allowSetters = true)
    private Set<TimesheetProject> projects = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimesheetTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TimesheetTask name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public TimesheetTask enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getConstant() {
        return this.constant;
    }

    public TimesheetTask constant(Integer constant) {
        this.setConstant(constant);
        return this;
    }

    public void setConstant(Integer constant) {
        this.constant = constant;
    }

    public Boolean getConstantEditable() {
        return this.constantEditable;
    }

    public TimesheetTask constantEditable(Boolean constantEditable) {
        this.setConstantEditable(constantEditable);
        return this;
    }

    public void setConstantEditable(Boolean constantEditable) {
        this.constantEditable = constantEditable;
    }

    public BigDecimal getFactor() {
        return this.factor;
    }

    public TimesheetTask factor(BigDecimal factor) {
        this.setFactor(factor);
        return this;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public Boolean getFactorEditable() {
        return this.factorEditable;
    }

    public TimesheetTask factorEditable(Boolean factorEditable) {
        this.setFactorEditable(factorEditable);
        return this;
    }

    public void setFactorEditable(Boolean factorEditable) {
        this.factorEditable = factorEditable;
    }

    public Integer getDefaultTimespan() {
        return this.defaultTimespan;
    }

    public TimesheetTask defaultTimespan(Integer defaultTimespan) {
        this.setDefaultTimespan(defaultTimespan);
        return this;
    }

    public void setDefaultTimespan(Integer defaultTimespan) {
        this.defaultTimespan = defaultTimespan;
    }

    public Set<TimesheetProject> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<TimesheetProject> timesheetProjects) {
        if (this.projects != null) {
            this.projects.forEach(i -> i.removeTasks(this));
        }
        if (timesheetProjects != null) {
            timesheetProjects.forEach(i -> i.addTasks(this));
        }
        this.projects = timesheetProjects;
    }

    public TimesheetTask projects(Set<TimesheetProject> timesheetProjects) {
        this.setProjects(timesheetProjects);
        return this;
    }

    public TimesheetTask addProjects(TimesheetProject timesheetProject) {
        this.projects.add(timesheetProject);
        timesheetProject.getTasks().add(this);
        return this;
    }

    public TimesheetTask removeProjects(TimesheetProject timesheetProject) {
        this.projects.remove(timesheetProject);
        timesheetProject.getTasks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimesheetTask)) {
            return false;
        }
        return id != null && id.equals(((TimesheetTask) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimesheetTask{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", constant=" + getConstant() +
            ", constantEditable='" + getConstantEditable() + "'" +
            ", factor=" + getFactor() +
            ", factorEditable='" + getFactorEditable() + "'" +
            ", defaultTimespan=" + getDefaultTimespan() +
            "}";
    }
}
