package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimesheetTime.
 */
@Entity
@Table(name = "timesheet_time")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TimesheetTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start")
    private Instant start;

    @Column(name = "end")
    private Instant end;

    @NotNull
    @Column(name = "effective_time", nullable = false)
    private Integer effectiveTime;

    @NotNull
    @Column(name = "pause", nullable = false)
    private Integer pause;

    @NotNull
    @Size(max = 1000)
    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @Column(name = "edited_factor", precision = 21, scale = 2)
    private BigDecimal editedFactor;

    @Column(name = "edited_constant")
    private Integer editedConstant;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "member" }, allowSetters = true)
    private Timesheet timesheet;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "owner", "tasks" }, allowSetters = true)
    private TimesheetProject project;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "projects" }, allowSetters = true)
    private TimesheetTask task;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimesheetTime id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStart() {
        return this.start;
    }

    public TimesheetTime start(Instant start) {
        this.setStart(start);
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return this.end;
    }

    public TimesheetTime end(Instant end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Integer getEffectiveTime() {
        return this.effectiveTime;
    }

    public TimesheetTime effectiveTime(Integer effectiveTime) {
        this.setEffectiveTime(effectiveTime);
        return this;
    }

    public void setEffectiveTime(Integer effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Integer getPause() {
        return this.pause;
    }

    public TimesheetTime pause(Integer pause) {
        this.setPause(pause);
        return this;
    }

    public void setPause(Integer pause) {
        this.pause = pause;
    }

    public String getDescription() {
        return this.description;
    }

    public TimesheetTime description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getEditedFactor() {
        return this.editedFactor;
    }

    public TimesheetTime editedFactor(BigDecimal editedFactor) {
        this.setEditedFactor(editedFactor);
        return this;
    }

    public void setEditedFactor(BigDecimal editedFactor) {
        this.editedFactor = editedFactor;
    }

    public Integer getEditedConstant() {
        return this.editedConstant;
    }

    public TimesheetTime editedConstant(Integer editedConstant) {
        this.setEditedConstant(editedConstant);
        return this;
    }

    public void setEditedConstant(Integer editedConstant) {
        this.editedConstant = editedConstant;
    }

    public Timesheet getTimesheet() {
        return this.timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public TimesheetTime timesheet(Timesheet timesheet) {
        this.setTimesheet(timesheet);
        return this;
    }

    public TimesheetProject getProject() {
        return this.project;
    }

    public void setProject(TimesheetProject timesheetProject) {
        this.project = timesheetProject;
    }

    public TimesheetTime project(TimesheetProject timesheetProject) {
        this.setProject(timesheetProject);
        return this;
    }

    public TimesheetTask getTask() {
        return this.task;
    }

    public void setTask(TimesheetTask timesheetTask) {
        this.task = timesheetTask;
    }

    public TimesheetTime task(TimesheetTask timesheetTask) {
        this.setTask(timesheetTask);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimesheetTime)) {
            return false;
        }
        return id != null && id.equals(((TimesheetTime) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimesheetTime{" +
            "id=" + getId() +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            ", effectiveTime=" + getEffectiveTime() +
            ", pause=" + getPause() +
            ", description='" + getDescription() + "'" +
            ", editedFactor=" + getEditedFactor() +
            ", editedConstant=" + getEditedConstant() +
            "}";
    }
}
