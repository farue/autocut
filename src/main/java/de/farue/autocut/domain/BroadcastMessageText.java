package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BroadcastMessageText.
 */
@Entity
@Table(name = "broadcast_message_text")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BroadcastMessageText implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 2)
    @Column(name = "lang_key", length = 2, nullable = false)
    private String langKey;

    @NotNull
    @Size(max = 4000)
    @Column(name = "text", length = 4000, nullable = false)
    private String text;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "messageTexts" }, allowSetters = true)
    private BroadcastMessage message;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BroadcastMessageText id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLangKey() {
        return this.langKey;
    }

    public BroadcastMessageText langKey(String langKey) {
        this.setLangKey(langKey);
        return this;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getText() {
        return this.text;
    }

    public BroadcastMessageText text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BroadcastMessage getMessage() {
        return this.message;
    }

    public void setMessage(BroadcastMessage broadcastMessage) {
        this.message = broadcastMessage;
    }

    public BroadcastMessageText message(BroadcastMessage broadcastMessage) {
        this.setMessage(broadcastMessage);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BroadcastMessageText)) {
            return false;
        }
        return id != null && id.equals(((BroadcastMessageText) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BroadcastMessageText{" +
            "id=" + getId() +
            ", langKey='" + getLangKey() + "'" +
            ", text='" + getText() + "'" +
            "}";
    }
}
