package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.LaundryMachineType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LaundryMachine.
 */
@Entity
@Table(name = "wash_machine")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LaundryMachine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "identifier", nullable = false)
    private String identifier;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LaundryMachineType type;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @NotNull
    @Column(name = "position_x", nullable = false)
    private Integer positionX;

    @NotNull
    @Column(name = "position_y", nullable = false)
    private Integer positionY;

    @OneToMany(mappedBy = "machine")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "program", "machine" }, allowSetters = true)
    private Set<LaundryMachineProgram> programs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LaundryMachine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public LaundryMachine identifier(String identifier) {
        this.setIdentifier(identifier);
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return this.name;
    }

    public LaundryMachine name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LaundryMachineType getType() {
        return this.type;
    }

    public LaundryMachine type(LaundryMachineType type) {
        this.setType(type);
        return this;
    }

    public void setType(LaundryMachineType type) {
        this.type = type;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public LaundryMachine enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getPositionX() {
        return this.positionX;
    }

    public LaundryMachine positionX(Integer positionX) {
        this.setPositionX(positionX);
        return this;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return this.positionY;
    }

    public LaundryMachine positionY(Integer positionY) {
        this.setPositionY(positionY);
        return this;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }

    public Set<LaundryMachineProgram> getPrograms() {
        return this.programs;
    }

    public void setPrograms(Set<LaundryMachineProgram> laundryMachinePrograms) {
        if (this.programs != null) {
            this.programs.forEach(i -> i.setMachine(null));
        }
        if (laundryMachinePrograms != null) {
            laundryMachinePrograms.forEach(i -> i.setMachine(this));
        }
        this.programs = laundryMachinePrograms;
    }

    public LaundryMachine programs(Set<LaundryMachineProgram> laundryMachinePrograms) {
        this.setPrograms(laundryMachinePrograms);
        return this;
    }

    public LaundryMachine addPrograms(LaundryMachineProgram laundryMachineProgram) {
        this.programs.add(laundryMachineProgram);
        laundryMachineProgram.setMachine(this);
        return this;
    }

    public LaundryMachine removePrograms(LaundryMachineProgram laundryMachineProgram) {
        this.programs.remove(laundryMachineProgram);
        laundryMachineProgram.setMachine(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaundryMachine)) {
            return false;
        }
        return id != null && id.equals(((LaundryMachine) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaundryMachine{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", positionX=" + getPositionX() +
            ", positionY=" + getPositionY() +
            "}";
    }
}
