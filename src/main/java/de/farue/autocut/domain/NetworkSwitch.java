package de.farue.autocut.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A NetworkSwitch.
 */
@Entity
@Table(name = "network_switch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NetworkSwitch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "interface_name", nullable = false)
    private String interfaceName;

    @NotNull
    @Column(name = "ssh_host", nullable = false)
    private String sshHost;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public NetworkSwitch interfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getSshHost() {
        return sshHost;
    }

    public NetworkSwitch sshHost(String sshHost) {
        this.sshHost = sshHost;
        return this;
    }

    public void setSshHost(String sshHost) {
        this.sshHost = sshHost;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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

    // prettier-ignore
    @Override
    public String toString() {
        return "NetworkSwitch{" +
            "id=" + getId() +
            ", interfaceName='" + getInterfaceName() + "'" +
            ", sshHost='" + getSshHost() + "'" +
            "}";
    }
}
