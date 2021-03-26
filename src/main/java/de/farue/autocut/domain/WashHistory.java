package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.WashHistoryStatus;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WashHistory.
 */
@Entity
@Table(name = "wash_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WashHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "using_date")
    private Instant usingDate;

    @Column(name = "reservation_date")
    private Instant reservationDate;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WashHistoryStatus status;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Tenant reservationTenant;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Tenant usingTenant;

    @ManyToOne
    @JsonIgnoreProperties(value = { "programs" }, allowSetters = true)
    private LaundryMachine machine;

    @ManyToOne
    @JsonIgnoreProperties(value = { "laundryMachine" }, allowSetters = true)
    private LaundryMachineProgram program;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WashHistory id(Long id) {
        this.id = id;
        return this;
    }

    public Instant getUsingDate() {
        return this.usingDate;
    }

    public WashHistory usingDate(Instant usingDate) {
        this.usingDate = usingDate;
        return this;
    }

    public void setUsingDate(Instant usingDate) {
        this.usingDate = usingDate;
    }

    public Instant getReservationDate() {
        return this.reservationDate;
    }

    public WashHistory reservationDate(Instant reservationDate) {
        this.reservationDate = reservationDate;
        return this;
    }

    public void setReservationDate(Instant reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public WashHistory lastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public WashHistoryStatus getStatus() {
        return this.status;
    }

    public WashHistory status(WashHistoryStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(WashHistoryStatus status) {
        this.status = status;
    }

    public Tenant getReservationTenant() {
        return this.reservationTenant;
    }

    public WashHistory reservationTenant(Tenant tenant) {
        this.setReservationTenant(tenant);
        return this;
    }

    public void setReservationTenant(Tenant tenant) {
        this.reservationTenant = tenant;
    }

    public Tenant getUsingTenant() {
        return this.usingTenant;
    }

    public WashHistory usingTenant(Tenant tenant) {
        this.setUsingTenant(tenant);
        return this;
    }

    public void setUsingTenant(Tenant tenant) {
        this.usingTenant = tenant;
    }

    public LaundryMachine getMachine() {
        return this.machine;
    }

    public WashHistory machine(LaundryMachine laundryMachine) {
        this.setMachine(laundryMachine);
        return this;
    }

    public void setMachine(LaundryMachine laundryMachine) {
        this.machine = laundryMachine;
    }

    public LaundryMachineProgram getProgram() {
        return this.program;
    }

    public WashHistory program(LaundryMachineProgram laundryMachineProgram) {
        this.setProgram(laundryMachineProgram);
        return this;
    }

    public void setProgram(LaundryMachineProgram laundryMachineProgram) {
        this.program = laundryMachineProgram;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WashHistory)) {
            return false;
        }
        return id != null && id.equals(((WashHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WashHistory{" +
            "id=" + getId() +
            ", usingDate='" + getUsingDate() + "'" +
            ", reservationDate='" + getReservationDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
