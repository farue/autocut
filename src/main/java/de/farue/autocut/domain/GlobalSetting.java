package de.farue.autocut.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GlobalSetting.
 */
@Entity
@Table(name = "global_setting")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GlobalSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String WASHING_PRICE_WASHING_MACHINE = "washing.price.washingmachine";
    public static final String WASHING_PRICE_DRYER = "washing.price.dryer";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "setting_key")
    private String key;

    @Column(name = "setting_value")
    private String value;

    @Column(name = "setting_type")
    private String valueType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GlobalSetting id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public GlobalSetting key(String key) {
        this.setKey(key);
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public GlobalSetting value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return this.valueType;
    }

    public GlobalSetting valueType(String valueType) {
        this.setValueType(valueType);
        return this;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GlobalSetting)) {
            return false;
        }
        return id != null && id.equals(((GlobalSetting) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GlobalSetting{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", valueType='" + getValueType() + "'" +
            "}";
    }
}
