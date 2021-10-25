package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.farue.autocut.domain.enumeration.BroadcastMessageType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BroadcastMessage.
 */
@Entity
@Table(name = "broadcast_message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BroadcastMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BroadcastMessageType type;

    @Column(name = "start")
    private Instant start;

    @Column(name = "end")
    private Instant end;

    @Column(name = "users_only")
    private Boolean usersOnly;

    @Column(name = "dismissible")
    private Boolean dismissible;

    @OneToMany(mappedBy = "message")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "message" }, allowSetters = true)
    private Set<BroadcastMessageText> messageTexts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BroadcastMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BroadcastMessageType getType() {
        return this.type;
    }

    public BroadcastMessage type(BroadcastMessageType type) {
        this.setType(type);
        return this;
    }

    public void setType(BroadcastMessageType type) {
        this.type = type;
    }

    public Instant getStart() {
        return this.start;
    }

    public BroadcastMessage start(Instant start) {
        this.setStart(start);
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return this.end;
    }

    public BroadcastMessage end(Instant end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Boolean getUsersOnly() {
        return this.usersOnly;
    }

    public BroadcastMessage usersOnly(Boolean usersOnly) {
        this.setUsersOnly(usersOnly);
        return this;
    }

    public void setUsersOnly(Boolean usersOnly) {
        this.usersOnly = usersOnly;
    }

    public Boolean getDismissible() {
        return this.dismissible;
    }

    public BroadcastMessage dismissible(Boolean dismissible) {
        this.setDismissible(dismissible);
        return this;
    }

    public void setDismissible(Boolean dismissible) {
        this.dismissible = dismissible;
    }

    public Set<BroadcastMessageText> getMessageTexts() {
        return this.messageTexts;
    }

    public void setMessageTexts(Set<BroadcastMessageText> broadcastMessageTexts) {
        if (this.messageTexts != null) {
            this.messageTexts.forEach(i -> i.setMessage(null));
        }
        if (broadcastMessageTexts != null) {
            broadcastMessageTexts.forEach(i -> i.setMessage(this));
        }
        this.messageTexts = broadcastMessageTexts;
    }

    public BroadcastMessage messageTexts(Set<BroadcastMessageText> broadcastMessageTexts) {
        this.setMessageTexts(broadcastMessageTexts);
        return this;
    }

    public BroadcastMessage addMessageText(BroadcastMessageText broadcastMessageText) {
        this.messageTexts.add(broadcastMessageText);
        broadcastMessageText.setMessage(this);
        return this;
    }

    public BroadcastMessage removeMessageText(BroadcastMessageText broadcastMessageText) {
        this.messageTexts.remove(broadcastMessageText);
        broadcastMessageText.setMessage(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BroadcastMessage)) {
            return false;
        }
        return id != null && id.equals(((BroadcastMessage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BroadcastMessage{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            ", usersOnly='" + getUsersOnly() + "'" +
            ", dismissible='" + getDismissible() + "'" +
            "}";
    }
}
