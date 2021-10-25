package de.farue.autocut.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Communication.
 */
@Entity
@Table(name = "communication")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Communication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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
    private User tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Communication id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return this.subject;
    }

    public Communication subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return this.text;
    }

    public Communication text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNote() {
        return this.note;
    }

    public Communication note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getDate() {
        return this.date;
    }

    public Communication date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public User getTenant() {
        return this.tenant;
    }

    public void setTenant(User user) {
        this.tenant = user;
    }

    public Communication tenant(User user) {
        this.setTenant(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Communication)) {
            return false;
        }
        return id != null && id.equals(((Communication) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Communication{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            ", text='" + getText() + "'" +
            ", note='" + getNote() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
