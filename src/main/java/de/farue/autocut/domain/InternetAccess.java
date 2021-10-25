package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A InternetAccess.
 */
@Entity
@Table(name = "internet_access")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InternetAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "ip_1", nullable = false)
    private String ip1;

    @NotNull
    @Column(name = "ip_2", nullable = false)
    private String ip2;

    @NotNull
    @Column(name = "switch_interface", nullable = false)
    private String switchInterface;

    @NotNull
    @Min(value = 1)
    @Column(name = "port", nullable = false)
    private Integer port;

    @ManyToOne
    private NetworkSwitch networkSwitch;

    @JsonIgnoreProperties(value = { "internetAccess", "leases", "address" }, allowSetters = true)
    @OneToOne(mappedBy = "internetAccess")
    private Apartment apartment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InternetAccess id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp1() {
        return this.ip1;
    }

    public InternetAccess ip1(String ip1) {
        this.setIp1(ip1);
        return this;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getIp2() {
        return this.ip2;
    }

    public InternetAccess ip2(String ip2) {
        this.setIp2(ip2);
        return this;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getSwitchInterface() {
        return this.switchInterface;
    }

    public InternetAccess switchInterface(String switchInterface) {
        this.setSwitchInterface(switchInterface);
        return this;
    }

    public void setSwitchInterface(String switchInterface) {
        this.switchInterface = switchInterface;
    }

    public Integer getPort() {
        return this.port;
    }

    public InternetAccess port(Integer port) {
        this.setPort(port);
        return this;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public NetworkSwitch getNetworkSwitch() {
        return this.networkSwitch;
    }

    public void setNetworkSwitch(NetworkSwitch networkSwitch) {
        this.networkSwitch = networkSwitch;
    }

    public InternetAccess networkSwitch(NetworkSwitch networkSwitch) {
        this.setNetworkSwitch(networkSwitch);
        return this;
    }

    public Apartment getApartment() {
        return this.apartment;
    }

    public void setApartment(Apartment apartment) {
        if (this.apartment != null) {
            this.apartment.setInternetAccess(null);
        }
        if (apartment != null) {
            apartment.setInternetAccess(this);
        }
        this.apartment = apartment;
    }

    public InternetAccess apartment(Apartment apartment) {
        this.setApartment(apartment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InternetAccess{" +
            "id=" + getId() +
            ", ip1='" + getIp1() + "'" +
            ", ip2='" + getIp2() + "'" +
            ", switchInterface='" + getSwitchInterface() + "'" +
            ", port=" + getPort() +
            "}";
    }
}
