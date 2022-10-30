package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RewardPayout.
 */
@Entity
@Table(name = "reward_payout")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RewardPayout implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp")
    private Instant timestamp;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "time")
    private Integer time;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "member" }, allowSetters = true)
    private Timesheet timesheet;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RewardPayout id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public RewardPayout timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public RewardPayout amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTime() {
        return this.time;
    }

    public RewardPayout time(Integer time) {
        this.setTime(time);
        return this;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Timesheet getTimesheet() {
        return this.timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public RewardPayout timesheet(Timesheet timesheet) {
        this.setTimesheet(timesheet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RewardPayout)) {
            return false;
        }
        return id != null && id.equals(((RewardPayout) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RewardPayout{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", amount=" + getAmount() +
            ", time=" + getTime() +
            "}";
    }
}
