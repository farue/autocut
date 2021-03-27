package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TenantCommunication.
 */
@Entity
@Table(name = "tenant_communication")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TenantCommunication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "subject", length = 80, nullable = false)
    private String subject;

    @Lob
    @Column(name = "text", nullable = false)
    private String text;

    @Lob
    @Column(name = "note")
    private String note;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "securityPolicies", "lease" }, allowSetters = true)
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TenantCommunication id(Long id) {
        this.id = id;
        return this;
    }

    public String getSubject() {
        return this.subject;
    }

    public TenantCommunication subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return this.text;
    }

    public TenantCommunication text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNote() {
        return this.note;
    }

    public TenantCommunication note(String note) {
        this.note = note;
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getDate() {
        return this.date;
    }

    public TenantCommunication date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public TenantCommunication tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantCommunication)) {
            return false;
        }
        return id != null && id.equals(((TenantCommunication) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantCommunication{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            ", text='" + getText() + "'" +
            ", note='" + getNote() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
