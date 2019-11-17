package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A InternetAccess.
 */
@Entity
@Table(name = "internet_access")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InternetAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "blocked", nullable = false)
    private Boolean blocked;

    @NotNull
    @Column(name = "ip_1", nullable = false)
    private String ip1;

    @NotNull
    @Column(name = "ip_2", nullable = false)
    private String ip2;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull

    @JoinColumn(unique = true)
    private Port port;

    @OneToOne(mappedBy = "internetAccess")
    @JsonIgnore
    private Apartment apartment;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public InternetAccess blocked(Boolean blocked) {
        this.blocked = blocked;
        return this;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getIp1() {
        return ip1;
    }

    public InternetAccess ip1(String ip1) {
        this.ip1 = ip1;
        return this;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getIp2() {
        return ip2;
    }

    public InternetAccess ip2(String ip2) {
        this.ip2 = ip2;
        return this;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public Port getPort() {
        return port;
    }

    public InternetAccess port(Port port) {
        this.port = port;
        return this;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public InternetAccess apartment(Apartment apartment) {
        this.apartment = apartment;
        return this;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternetAccess)) {
            return false;
        }
        return id != null && id.equals(((InternetAccess) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "InternetAccess{" +
            "id=" + getId() +
            ", blocked='" + isBlocked() + "'" +
            ", ip1='" + getIp1() + "'" +
            ", ip2='" + getIp2() + "'" +
            "}";
    }
}
