package de.farue.autocut.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
    @Column(name = "id")
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

    @Column(name = "timestamp")
    private Instant timestamp;

    @ManyToOne
    private NetworkSwitch networkSwitch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NetworkSwitchStatus id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPort() {
        return this.port;
    }

    public NetworkSwitchStatus port(String port) {
        this.setPort(port);
        return this;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return this.name;
    }

    public NetworkSwitchStatus name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return this.status;
    }

    public NetworkSwitchStatus status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVlan() {
        return this.vlan;
    }

    public NetworkSwitchStatus vlan(String vlan) {
        this.setVlan(vlan);
        return this;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public String getSpeed() {
        return this.speed;
    }

    public NetworkSwitchStatus speed(String speed) {
        this.setSpeed(speed);
        return this;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getType() {
        return this.type;
    }

    public NetworkSwitchStatus type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public NetworkSwitchStatus timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public NetworkSwitch getNetworkSwitch() {
        return this.networkSwitch;
    }

    public void setNetworkSwitch(NetworkSwitch networkSwitch) {
        this.networkSwitch = networkSwitch;
    }

    public NetworkSwitchStatus networkSwitch(NetworkSwitch networkSwitch) {
        this.setNetworkSwitch(networkSwitch);
        return this;
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
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
