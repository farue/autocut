package de.farue.autocut.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A Coin.
 */
@Entity
@Table(name = "coin")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Coin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "date_purchase")
    private Instant datePurchase;

    @Column(name = "date_redeem")
    private Instant dateRedeem;

    @ManyToOne
    @JsonIgnoreProperties("coins")
    private Lease tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public Coin token(String token) {
        this.token = token;
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getDatePurchase() {
        return datePurchase;
    }

    public Coin datePurchase(Instant datePurchase) {
        this.datePurchase = datePurchase;
        return this;
    }

    public void setDatePurchase(Instant datePurchase) {
        this.datePurchase = datePurchase;
    }

    public Instant getDateRedeem() {
        return dateRedeem;
    }

    public Coin dateRedeem(Instant dateRedeem) {
        this.dateRedeem = dateRedeem;
        return this;
    }

    public void setDateRedeem(Instant dateRedeem) {
        this.dateRedeem = dateRedeem;
    }

    public Lease getTenant() {
        return tenant;
    }

    public Coin tenant(Lease lease) {
        this.tenant = lease;
        return this;
    }

    public void setTenant(Lease lease) {
        this.tenant = lease;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coin)) {
            return false;
        }
        return id != null && id.equals(((Coin) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Coin{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", datePurchase='" + getDatePurchase() + "'" +
            ", dateRedeem='" + getDateRedeem() + "'" +
            "}";
    }
}
