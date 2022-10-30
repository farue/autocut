package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.RewardTimeReviewStatus;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RewardTimeReview.
 */
@Entity
@Table(name = "reward_time_review")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RewardTimeReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RewardTimeReviewStatus status;

    @Size(max = 4000)
    @Column(name = "comment", length = 4000)
    private String comment;

    @JsonIgnoreProperties(value = { "timesheet", "project", "task" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private TimesheetTime timesheetTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RewardTimeReview id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RewardTimeReviewStatus getStatus() {
        return this.status;
    }

    public RewardTimeReview status(RewardTimeReviewStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RewardTimeReviewStatus status) {
        this.status = status;
    }

    public String getComment() {
        return this.comment;
    }

    public RewardTimeReview comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public TimesheetTime getTimesheetTime() {
        return this.timesheetTime;
    }

    public void setTimesheetTime(TimesheetTime timesheetTime) {
        this.timesheetTime = timesheetTime;
    }

    public RewardTimeReview timesheetTime(TimesheetTime timesheetTime) {
        this.setTimesheetTime(timesheetTime);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RewardTimeReview)) {
            return false;
        }
        return id != null && id.equals(((RewardTimeReview) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RewardTimeReview{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", comment='" + getComment() + "'" +
            "}";
    }
}
