package de.farue.autocut.domain;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A NetworkSwitchStatus.
 */
@Entity
@Table(name = "network_switch_status")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NetworkSwitchStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "port")
    private String port;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;

    @Column(name = "vlan")
    private String vlan;

    @Column(name = "speed")
    private String speed;

    @Column(name = "type")
    private String type;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @ManyToOne
    @JsonIgnoreProperties(value = "networkSwitchStatuses", allowSetters = true)
    private NetworkSwitch networkSwitch;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPort() {
        return port;
    }

    public NetworkSwitchStatus port(String port) {
        this.port = port;
        return this;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public NetworkSwitchStatus name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public NetworkSwitchStatus status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVlan() {
        return vlan;
    }

    public NetworkSwitchStatus vlan(String vlan) {
        this.vlan = vlan;
        return this;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public String getSpeed() {
        return speed;
    }

    public NetworkSwitchStatus speed(String speed) {
        this.speed = speed;
        return this;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getType() {
        return type;
    }

    public NetworkSwitchStatus type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public NetworkSwitchStatus timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public NetworkSwitch getNetworkSwitch() {
        return networkSwitch;
    }

    public NetworkSwitchStatus networkSwitch(NetworkSwitch networkSwitch) {
        this.networkSwitch = networkSwitch;
        return this;
    }

    public void setNetworkSwitch(NetworkSwitch networkSwitch) {
        this.networkSwitch = networkSwitch;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkSwitchStatus)) {
            return false;
        }
        return id != null && id.equals(((NetworkSwitchStatus) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NetworkSwitchStatus{" +
            "id=" + getId() +
            ", port='" + getPort() + "'" +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            ", vlan='" + getVlan() + "'" +
            ", speed='" + getSpeed() + "'" +
            ", type='" + getType() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            "}";
    }
}
