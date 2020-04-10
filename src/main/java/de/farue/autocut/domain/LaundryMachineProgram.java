package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A LaundryMachineProgram.
 */
@Entity
@Table(name = "laundry_machine_program")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class LaundryMachineProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "time", nullable = false)
    private Integer time;

    @Column(name = "temperature")
    private Integer temperature;

    @Column(name = "spin")
    private Integer spin;

    @Column(name = "pre_wash")
    private Boolean preWash;

    @Column(name = "protect")
    private Boolean protect;

    @Column(name = "short_cycle")
    private Boolean shortCycle;

    @Column(name = "wrinkle")
    private Boolean wrinkle;

    @ManyToOne
    @JsonIgnoreProperties("programs")
    private LaundryMachine laundryMachine;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public LaundryMachineProgram name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTime() {
        return time;
    }

    public LaundryMachineProgram time(Integer time) {
        this.time = time;
        return this;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public LaundryMachineProgram temperature(Integer temperature) {
        this.temperature = temperature;
        return this;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getSpin() {
        return spin;
    }

    public LaundryMachineProgram spin(Integer spin) {
        this.spin = spin;
        return this;
    }

    public void setSpin(Integer spin) {
        this.spin = spin;
    }

    public Boolean isPreWash() {
        return preWash;
    }

    public LaundryMachineProgram preWash(Boolean preWash) {
        this.preWash = preWash;
        return this;
    }

    public void setPreWash(Boolean preWash) {
        this.preWash = preWash;
    }

    public Boolean isProtect() {
        return protect;
    }

    public LaundryMachineProgram protect(Boolean protect) {
        this.protect = protect;
        return this;
    }

    public void setProtect(Boolean protect) {
        this.protect = protect;
    }

    public Boolean isShortCycle() {
        return shortCycle;
    }

    public LaundryMachineProgram shortCycle(Boolean shortCycle) {
        this.shortCycle = shortCycle;
        return this;
    }

    public void setShortCycle(Boolean shortCycle) {
        this.shortCycle = shortCycle;
    }

    public Boolean isWrinkle() {
        return wrinkle;
    }

    public LaundryMachineProgram wrinkle(Boolean wrinkle) {
        this.wrinkle = wrinkle;
        return this;
    }

    public void setWrinkle(Boolean wrinkle) {
        this.wrinkle = wrinkle;
    }

    public LaundryMachine getLaundryMachine() {
        return laundryMachine;
    }

    public LaundryMachineProgram laundryMachine(LaundryMachine laundryMachine) {
        this.laundryMachine = laundryMachine;
        return this;
    }

    public void setLaundryMachine(LaundryMachine laundryMachine) {
        this.laundryMachine = laundryMachine;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaundryMachineProgram)) {
            return false;
        }
        return id != null && id.equals(((LaundryMachineProgram) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "LaundryMachineProgram{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", time=" + getTime() +
            ", temperature=" + getTemperature() +
            ", spin=" + getSpin() +
            ", preWash='" + isPreWash() + "'" +
            ", protect='" + isProtect() + "'" +
            ", shortCycle='" + isShortCycle() + "'" +
            ", wrinkle='" + isWrinkle() + "'" +
            "}";
    }
}
