package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimesheetProject.
 */
@Entity
@Table(name = "timesheet_project")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TimesheetProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start")
    private LocalDate start;

    @Column(name = "end")
    private LocalDate end;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Tenant owner;

    @ManyToMany
    @JoinTable(
        name = "rel_timesheet_project__tasks",
        joinColumns = @JoinColumn(name = "timesheet_project_id"),
        inverseJoinColumns = @JoinColumn(name = "tasks_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "projects" }, allowSetters = true)
    private Set<TimesheetTask> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimesheetProject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TimesheetProject name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public TimesheetProject start(LocalDate start) {
        this.setStart(start);
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public TimesheetProject end(LocalDate end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Tenant getOwner() {
        return this.owner;
    }

    public void setOwner(Tenant tenant) {
        this.owner = tenant;
    }

    public TimesheetProject owner(Tenant tenant) {
        this.setOwner(tenant);
        return this;
    }

    public Set<TimesheetTask> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<TimesheetTask> timesheetTasks) {
        this.tasks = timesheetTasks;
    }

    public TimesheetProject tasks(Set<TimesheetTask> timesheetTasks) {
        this.setTasks(timesheetTasks);
        return this;
    }

    public TimesheetProject addTasks(TimesheetTask timesheetTask) {
        this.tasks.add(timesheetTask);
        timesheetTask.getProjects().add(this);
        return this;
    }

    public TimesheetProject removeTasks(TimesheetTask timesheetTask) {
        this.tasks.remove(timesheetTask);
        timesheetTask.getProjects().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimesheetProject)) {
            return false;
        }
        return id != null && id.equals(((TimesheetProject) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimesheetProject{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            "}";
    }
}
