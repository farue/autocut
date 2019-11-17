package de.farue.autocut.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A NetworkSwitch.
 */
@Entity
@Table(name = "network_switch")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NetworkSwitch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "switch_interface", nullable = false)
    private String switchInterface;

    @OneToMany(mappedBy = "networkSwitch")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Port> ports = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSwitchInterface() {
        return switchInterface;
    }

    public NetworkSwitch switchInterface(String switchInterface) {
        this.switchInterface = switchInterface;
        return this;
    }

    public void setSwitchInterface(String switchInterface) {
        this.switchInterface = switchInterface;
    }

    public Set<Port> getPorts() {
        return ports;
    }

    public NetworkSwitch ports(Set<Port> ports) {
        this.ports = ports;
        return this;
    }

    public NetworkSwitch addPorts(Port port) {
        this.ports.add(port);
        port.setNetworkSwitch(this);
        return this;
    }

    public NetworkSwitch removePorts(Port port) {
        this.ports.remove(port);
        port.setNetworkSwitch(null);
        return this;
    }

    public void setPorts(Set<Port> ports) {
        this.ports = ports;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkSwitch)) {
            return false;
        }
        return id != null && id.equals(((NetworkSwitch) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "NetworkSwitch{" +
            "id=" + getId() +
            ", switchInterface='" + getSwitchInterface() + "'" +
            "}";
    }
}
