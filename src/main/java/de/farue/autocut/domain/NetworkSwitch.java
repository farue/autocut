package de.farue.autocut.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
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

    @NotNull
    @Min(value = 0)
    @Max(value = 65535)
    @Column(name = "ssh_port", nullable = false)
    private Integer sshPort;

    @Lob
    @Column(name = "ssh_key", nullable = false)
    private String sshKey;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NetworkSwitch id(Long id) {
        this.id = id;
        return this;
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public NetworkSwitch interfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getSshHost() {
        return this.sshHost;
    }

    public NetworkSwitch sshHost(String sshHost) {
        this.sshHost = sshHost;
        return this;
    }

    public void setSshHost(String sshHost) {
        this.sshHost = sshHost;
    }

    public Integer getSshPort() {
        return this.sshPort;
    }

    public NetworkSwitch sshPort(Integer sshPort) {
        this.sshPort = sshPort;
        return this;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getSshKey() {
        return this.sshKey;
    }

    public NetworkSwitch sshKey(String sshKey) {
        this.sshKey = sshKey;
        return this;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NetworkSwitch{" +
            "id=" + getId() +
            ", interfaceName='" + getInterfaceName() + "'" +
            ", sshHost='" + getSshHost() + "'" +
            ", sshPort=" + getSshPort() +
            ", sshKey='" + getSshKey() + "'" +
            "}";
    }
}
