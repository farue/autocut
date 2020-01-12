package de.farue.autocut.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A Port.
 */
@Entity
@Table(name = "port")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Port implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "number", nullable = false)
    private Integer number;

    @OneToOne(mappedBy = "port")
    @JsonIgnore
    private InternetAccess internetAccess;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("ports")
    private NetworkSwitch networkSwitch;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public Port number(Integer number) {
        this.number = number;
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public InternetAccess getInternetAccess() {
        return internetAccess;
    }

    public Port internetAccess(InternetAccess internetAccess) {
        this.internetAccess = internetAccess;
        return this;
    }

    public void setInternetAccess(InternetAccess internetAccess) {
        this.internetAccess = internetAccess;
    }

    public NetworkSwitch getNetworkSwitch() {
        return networkSwitch;
    }

    public Port networkSwitch(NetworkSwitch networkSwitch) {
        this.networkSwitch = networkSwitch;
        return this;
    }

    public void setNetworkSwitch(NetworkSwitch networkSwitch) {
        this.networkSwitch = networkSwitch;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Port)) {
            return false;
        }
        return id != null && id.equals(((Port) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Port{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            "}";
    }
}
