package de.farue.autocut.domain;

import de.farue.autocut.domain.enumeration.ScheduledJobStatus;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "scheduled_job")
public class ScheduledJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ScheduledJobStatus status;

    @Column(name = "exit_message")
    private String exitMessage;

    @Column(name = "data_start_time")
    private Instant dataStartTime;

    @Column(name = "data_end_time")
    private Instant dataEndTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduledJob id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ScheduledJob name(String name) {
        this.name = name;
        return this;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public ScheduledJob startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public ScheduledJob endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public ScheduledJobStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduledJobStatus status) {
        this.status = status;
    }

    public ScheduledJob status(ScheduledJobStatus status) {
        this.status = status;
        return this;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }

    public ScheduledJob exitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
        return this;
    }

    public Instant getDataStartTime() {
        return dataStartTime;
    }

    public void setDataStartTime(Instant dataStartTime) {
        this.dataStartTime = dataStartTime;
    }

    public ScheduledJob dataStartTime(Instant dataStartTime) {
        this.dataStartTime = dataStartTime;
        return this;
    }

    public Instant getDataEndTime() {
        return dataEndTime;
    }

    public void setDataEndTime(Instant dataEndTime) {
        this.dataEndTime = dataEndTime;
    }

    public ScheduledJob dataEndTime(Instant dataEndTime) {
        this.dataEndTime = dataEndTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduledJob)) {
            return false;
        }
        return id != null && id.equals(((ScheduledJob) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduledJob{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", exitMessage='" + getExitMessage() + "'" +
            ", dataStartTime='" + getDataStartTime() + "'" +
            ", dataEndTime='" + getDataEndTime() + "'" +
            "}";
    }
}
