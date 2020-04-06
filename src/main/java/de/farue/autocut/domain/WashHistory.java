package de.farue.autocut.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A WashHistory.
 */
@Entity
@Table(name = "wash_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WashHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private Instant date;

    @Column(name = "reservation")
    private Instant reservation;

    @OneToOne
    @JoinColumn(unique = true)
    private Coin coin;

    @ManyToOne
    @JsonIgnoreProperties("washHistories")
    private Tenant tenant;

    @ManyToOne
    @JsonIgnoreProperties("washHistories")
    private LaundryMachine machine;

    @ManyToOne
    @JsonIgnoreProperties("washHistories")
    private LaundryMachineProgram program;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public WashHistory date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getReservation() {
        return reservation;
    }

    public WashHistory reservation(Instant reservation) {
        this.reservation = reservation;
        return this;
    }

    public void setReservation(Instant reservation) {
        this.reservation = reservation;
    }

    public Coin getCoin() {
        return coin;
    }

    public WashHistory coin(Coin coin) {
        this.coin = coin;
        return this;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public WashHistory tenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public LaundryMachine getMachine() {
        return machine;
    }

    public WashHistory machine(LaundryMachine laundryMachine) {
        this.machine = laundryMachine;
        return this;
    }

    public void setMachine(LaundryMachine laundryMachine) {
        this.machine = laundryMachine;
    }

    public LaundryMachineProgram getProgram() {
        return program;
    }

    public WashHistory program(LaundryMachineProgram laundryMachineProgram) {
        this.program = laundryMachineProgram;
        return this;
    }

    public void setProgram(LaundryMachineProgram laundryMachineProgram) {
        this.program = laundryMachineProgram;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

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
        return 31;
    }

    @Override
    public String toString() {
        return "WashHistory{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", reservation='" + getReservation() + "'" +
            "}";
    }
}
