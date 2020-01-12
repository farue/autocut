package de.farue.autocut.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A PaymentEntry.
 */
@Entity
@Table(name = "payment_entry")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PaymentEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "balance_before", precision = 21, scale = 2, nullable = false)
    private BigDecimal balanceBefore;

    @NotNull
    @Column(name = "balance_after", precision = 21, scale = 2, nullable = false)
    private BigDecimal balanceAfter;

    @NotNull
    @Column(name = "payment", precision = 21, scale = 2, nullable = false)
    private BigDecimal payment;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(unique = true)
    private Transaction transaction;

    @ManyToOne
    @JsonIgnoreProperties("paymentEntries")
    private PaymentAccount account;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public PaymentEntry balanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
        return this;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public PaymentEntry balanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
        return this;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public PaymentEntry payment(BigDecimal payment) {
        this.payment = payment;
        return this;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Instant getDate() {
        return date;
    }

    public PaymentEntry date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public PaymentEntry description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public PaymentEntry transaction(Transaction transaction) {
        this.transaction = transaction;
        return this;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public PaymentAccount getAccount() {
        return account;
    }

    public PaymentEntry account(PaymentAccount paymentAccount) {
        this.account = paymentAccount;
        return this;
    }

    public void setAccount(PaymentAccount paymentAccount) {
        this.account = paymentAccount;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentEntry)) {
            return false;
        }
        return id != null && id.equals(((PaymentEntry) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PaymentEntry{" +
            "id=" + getId() +
            ", balanceBefore=" + getBalanceBefore() +
            ", balanceAfter=" + getBalanceAfter() +
            ", payment=" + getPayment() +
            ", date='" + getDate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
