package de.farue.autocut.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LaundryProgram.
 */
@Entity
@Table(name = "wash_program")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LaundryProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "subprogram")
    private String subprogram;

    @Column(name = "spin")
    private Integer spin;

    @Column(name = "pre_wash")
    private Boolean preWash;

    @Column(name = "protect")
    private Boolean protect;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LaundryProgram id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public LaundryProgram name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubprogram() {
        return this.subprogram;
    }

    public LaundryProgram subprogram(String subprogram) {
        this.subprogram = subprogram;
        return this;
    }

    public void setSubprogram(String subprogram) {
        this.subprogram = subprogram;
    }

    public Integer getSpin() {
        return this.spin;
    }

    public LaundryProgram spin(Integer spin) {
        this.spin = spin;
        return this;
    }

    public void setSpin(Integer spin) {
        this.spin = spin;
    }

    public Boolean getPreWash() {
        return this.preWash;
    }

    public LaundryProgram preWash(Boolean preWash) {
        this.preWash = preWash;
        return this;
    }

    public void setPreWash(Boolean preWash) {
        this.preWash = preWash;
    }

    public Boolean getProtect() {
        return this.protect;
    }

    public LaundryProgram protect(Boolean protect) {
        this.protect = protect;
        return this;
    }

    public void setProtect(Boolean protect) {
        this.protect = protect;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaundryProgram)) {
            return false;
        }
        return id != null && id.equals(((LaundryProgram) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaundryProgram{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", subprogram='" + getSubprogram() + "'" +
            ", spin=" + getSpin() +
            ", preWash='" + getPreWash() + "'" +
            ", protect='" + getProtect() + "'" +
            "}";
    }
}
