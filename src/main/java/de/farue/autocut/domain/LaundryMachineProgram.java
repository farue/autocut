package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LaundryMachineProgram.
 */
@Entity
@Table(name = "wash_machine_program")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LaundryMachineProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "time", nullable = false)
    private Integer time;

    @ManyToOne(optional = false)
    @NotNull
    private LaundryProgram program;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "programs" }, allowSetters = true)
    private LaundryMachine machine;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LaundryMachineProgram id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getTime() {
        return this.time;
    }

    public LaundryMachineProgram time(Integer time) {
        this.time = time;
        return this;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public LaundryProgram getProgram() {
        return this.program;
    }

    public LaundryMachineProgram program(LaundryProgram laundryProgram) {
        this.setProgram(laundryProgram);
        return this;
    }

    public void setProgram(LaundryProgram laundryProgram) {
        this.program = laundryProgram;
    }

    public LaundryMachine getMachine() {
        return this.machine;
    }

    public LaundryMachineProgram machine(LaundryMachine laundryMachine) {
        this.setMachine(laundryMachine);
        return this;
    }

    public void setMachine(LaundryMachine laundryMachine) {
        this.machine = laundryMachine;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaundryMachineProgram{" +
            "id=" + getId() +
            ", time=" + getTime() +
            "}";
    }
}
