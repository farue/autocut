package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LaundryMachineProgram.
 */
@Entity
@Table(name = "laundry_machine_program")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LaundryMachineProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "subprogram")
    private String subprogram;

    @NotNull
    @Column(name = "time", nullable = false)
    private Integer time;

    @Column(name = "spin")
    private Integer spin;

    @Column(name = "pre_wash")
    private Boolean preWash;

    @Column(name = "protect")
    private Boolean protect;

    @ManyToOne
    @JsonIgnoreProperties(value = { "programs" }, allowSetters = true)
    private LaundryMachine laundryMachine;

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

    public String getName() {
        return this.name;
    }

    public LaundryMachineProgram name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubprogram() {
        return this.subprogram;
    }

    public LaundryMachineProgram subprogram(String subprogram) {
        this.subprogram = subprogram;
        return this;
    }

    public void setSubprogram(String subprogram) {
        this.subprogram = subprogram;
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

    public Integer getSpin() {
        return this.spin;
    }

    public LaundryMachineProgram spin(Integer spin) {
        this.spin = spin;
        return this;
    }

    public void setSpin(Integer spin) {
        this.spin = spin;
    }

    public Boolean getPreWash() {
        return this.preWash;
    }

    public LaundryMachineProgram preWash(Boolean preWash) {
        this.preWash = preWash;
        return this;
    }

    public void setPreWash(Boolean preWash) {
        this.preWash = preWash;
    }

    public Boolean getProtect() {
        return this.protect;
    }

    public LaundryMachineProgram protect(Boolean protect) {
        this.protect = protect;
        return this;
    }

    public void setProtect(Boolean protect) {
        this.protect = protect;
    }

    public LaundryMachine getLaundryMachine() {
        return this.laundryMachine;
    }

    public LaundryMachineProgram laundryMachine(LaundryMachine laundryMachine) {
        this.setLaundryMachine(laundryMachine);
        return this;
    }

    public void setLaundryMachine(LaundryMachine laundryMachine) {
        this.laundryMachine = laundryMachine;
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
            ", name='" + getName() + "'" +
            ", subprogram='" + getSubprogram() + "'" +
            ", time=" + getTime() +
            ", spin=" + getSpin() +
            ", preWash='" + getPreWash() + "'" +
            ", protect='" + getProtect() + "'" +
            "}";
    }
}
