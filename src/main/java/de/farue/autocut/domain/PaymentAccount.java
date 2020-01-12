package de.farue.autocut.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A PaymentAccount.
 */
@Entity
@Table(name = "payment_account")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PaymentAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "balance", precision = 21, scale = 2, nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "account")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PaymentEntry> paymentEntries = new HashSet<>();

    @OneToOne(mappedBy = "account")
    @JsonIgnore
    private Lease lease;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public PaymentAccount balance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Set<PaymentEntry> getPaymentEntries() {
        return paymentEntries;
    }

    public PaymentAccount paymentEntries(Set<PaymentEntry> paymentEntries) {
        this.paymentEntries = paymentEntries;
        return this;
    }

    public PaymentAccount addPaymentEntries(PaymentEntry paymentEntry) {
        this.paymentEntries.add(paymentEntry);
        paymentEntry.setAccount(this);
        return this;
    }

    public PaymentAccount removePaymentEntries(PaymentEntry paymentEntry) {
        this.paymentEntries.remove(paymentEntry);
        paymentEntry.setAccount(null);
        return this;
    }

    public void setPaymentEntries(Set<PaymentEntry> paymentEntries) {
        this.paymentEntries = paymentEntries;
    }

    public Lease getLease() {
        return lease;
    }

    public PaymentAccount lease(Lease lease) {
        this.lease = lease;
        return this;
    }

    public void setLease(Lease lease) {
        this.lease = lease;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentAccount)) {
            return false;
        }
        return id != null && id.equals(((PaymentAccount) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PaymentAccount{" +
            "id=" + getId() +
            ", balance=" + getBalance() +
            "}";
    }
}
