package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimesheetTimer.
 */
@Entity
@Table(name = "timesheet_timer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TimesheetTimer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start", nullable = false)
    private Instant start;

    @Column(name = "pause_start")
    private Instant pauseStart;

    @Column(name = "pause")
    private Integer pause;

    @JsonIgnoreProperties(value = { "member" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Timesheet timesheet;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimesheetTimer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStart() {
        return this.start;
    }

    public TimesheetTimer start(Instant start) {
        this.setStart(start);
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getPauseStart() {
        return this.pauseStart;
    }

    public TimesheetTimer pauseStart(Instant pauseStart) {
        this.setPauseStart(pauseStart);
        return this;
    }

    public void setPauseStart(Instant pauseStart) {
        this.pauseStart = pauseStart;
    }

    public Integer getPause() {
        return this.pause;
    }

    public TimesheetTimer pause(Integer pause) {
        this.setPause(pause);
        return this;
    }

    public void setPause(Integer pause) {
        this.pause = pause;
    }

    public Timesheet getTimesheet() {
        return this.timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public TimesheetTimer timesheet(Timesheet timesheet) {
        this.setTimesheet(timesheet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimesheetTimer)) {
            return false;
        }
        return id != null && id.equals(((TimesheetTimer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimesheetTimer{" +
            "id=" + getId() +
            ", start='" + getStart() + "'" +
            ", pauseStart='" + getPauseStart() + "'" +
            ", pause=" + getPause() +
            "}";
    }
}
