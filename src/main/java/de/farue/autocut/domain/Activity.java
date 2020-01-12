package de.farue.autocut.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "semester", nullable = false)
    private String semester;

    @Column(name = "date")
    private Instant date;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "discount", nullable = false)
    private Boolean discount;

    @NotNull
    @Column(name = "stw_activity", nullable = false)
    private Boolean stwActivity;

    @ManyToOne
    @JsonIgnoreProperties("activties")
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSemester() {
        return semester;
    }

    public Activity semester(String semester) {
        this.semester = semester;
        return this;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Instant getDate() {
        return date;
    }

    public Activity date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public Activity description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isDiscount() {
        return discount;
    }

    public Activity discount(Boolean discount) {
        this.discount = discount;
        return this;
    }

    public void setDiscount(Boolean discount) {
        this.discount = discount;
    }

    public Boolean isStwActivity() {
        return stwActivity;
    }

    public Activity stwActivity(Boolean stwActivity) {
        this.stwActivity = stwActivity;
        return this;
    }

    public void setStwActivity(Boolean stwActivity) {
        this.stwActivity = stwActivity;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Activity tenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Activity)) {
            return false;
        }
        return id != null && id.equals(((Activity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", semester='" + getSemester() + "'" +
            ", date='" + getDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", discount='" + isDiscount() + "'" +
            ", stwActivity='" + isStwActivity() + "'" +
            "}";
    }
}
