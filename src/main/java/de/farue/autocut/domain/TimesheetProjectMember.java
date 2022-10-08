package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimesheetProjectMember.
 */
@Entity
@Table(name = "timesheet_project_member")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TimesheetProjectMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start")
    private LocalDate start;

    @Column(name = "end")
    private LocalDate end;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "owner", "tasks" }, allowSetters = true)
    private TimesheetProject project;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "member" }, allowSetters = true)
    private Timesheet timesheet;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimesheetProjectMember id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public TimesheetProjectMember start(LocalDate start) {
        this.setStart(start);
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public TimesheetProjectMember end(LocalDate end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public TimesheetProject getProject() {
        return this.project;
    }

    public void setProject(TimesheetProject timesheetProject) {
        this.project = timesheetProject;
    }

    public TimesheetProjectMember project(TimesheetProject timesheetProject) {
        this.setProject(timesheetProject);
        return this;
    }

    public Timesheet getTimesheet() {
        return this.timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public TimesheetProjectMember timesheet(Timesheet timesheet) {
        this.setTimesheet(timesheet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimesheetProjectMember)) {
            return false;
        }
        return id != null && id.equals(((TimesheetProjectMember) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimesheetProjectMember{" +
            "id=" + getId() +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            "}";
    }
}
