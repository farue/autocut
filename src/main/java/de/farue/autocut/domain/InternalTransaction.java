package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A InternalTransaction.
 */
@Entity
@Table(name = "internal_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InternalTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "booking_date", nullable = false)
    private Instant bookingDate;

    @NotNull
    @Column(name = "value_date", nullable = false)
    private Instant valueDate;

    @NotNull
    @Column(name = "value", precision = 21, scale = 2, nullable = false)
    private BigDecimal value;

    @NotNull
    @Column(name = "balance_after", precision = 21, scale = 2, nullable = false)
    private BigDecimal balanceAfter;

    @Column(name = "description")
    private String description;

    @Column(name = "service_qulifier")
    private String serviceQulifier;

    @NotNull
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "recipient")
    private String recipient;

    @ManyToMany
    @JoinTable(
        name = "rel_internal_transaction__left",
        joinColumns = @JoinColumn(name = "internal_transaction_id"),
        inverseJoinColumns = @JoinColumn(name = "left_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<InternalTransaction> lefts = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "iTransactions", "bTransactions", "leases" }, allowSetters = true)
    private TransactionBook transactionBook;

    @ManyToMany(mappedBy = "lefts")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "lefts", "transactionBook", "rights" }, allowSetters = true)
    private Set<InternalTransaction> rights = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InternalTransaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public InternalTransaction type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getBookingDate() {
        return this.bookingDate;
    }

    public InternalTransaction bookingDate(Instant bookingDate) {
        this.setBookingDate(bookingDate);
        return this;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Instant getValueDate() {
        return this.valueDate;
    }

    public InternalTransaction valueDate(Instant valueDate) {
        this.setValueDate(valueDate);
        return this;
    }

    public void setValueDate(Instant valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public InternalTransaction value(BigDecimal value) {
        this.setValue(value);
        return this;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getBalanceAfter() {
        return this.balanceAfter;
    }

    public InternalTransaction balanceAfter(BigDecimal balanceAfter) {
        this.setBalanceAfter(balanceAfter);
        return this;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return this.description;
    }

    public InternalTransaction description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceQulifier() {
        return this.serviceQulifier;
    }

    public InternalTransaction serviceQulifier(String serviceQulifier) {
        this.setServiceQulifier(serviceQulifier);
        return this;
    }

    public void setServiceQulifier(String serviceQulifier) {
        this.serviceQulifier = serviceQulifier;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public InternalTransaction issuer(String issuer) {
        this.setIssuer(issuer);
        return this;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public InternalTransaction recipient(String recipient) {
        this.setRecipient(recipient);
        return this;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Set<InternalTransaction> getLefts() {
        return this.lefts;
    }

    public void setLefts(Set<InternalTransaction> internalTransactions) {
        this.lefts = internalTransactions;
    }

    public InternalTransaction lefts(Set<InternalTransaction> internalTransactions) {
        this.setLefts(internalTransactions);
        return this;
    }

    public InternalTransaction addLeft(InternalTransaction internalTransaction) {
        this.lefts.add(internalTransaction);
        internalTransaction.getRights().add(this);
        return this;
    }

    public InternalTransaction removeLeft(InternalTransaction internalTransaction) {
        this.lefts.remove(internalTransaction);
        internalTransaction.getRights().remove(this);
        return this;
    }

    public TransactionBook getTransactionBook() {
        return this.transactionBook;
    }

    public void setTransactionBook(TransactionBook transactionBook) {
        this.transactionBook = transactionBook;
    }

    public InternalTransaction transactionBook(TransactionBook transactionBook) {
        this.setTransactionBook(transactionBook);
        return this;
    }

    public Set<InternalTransaction> getRights() {
        return this.rights;
    }

    public void setRights(Set<InternalTransaction> internalTransactions) {
        if (this.rights != null) {
            this.rights.forEach(i -> i.removeLeft(this));
        }
        if (internalTransactions != null) {
            internalTransactions.forEach(i -> i.addLeft(this));
        }
        this.rights = internalTransactions;
    }

    public InternalTransaction rights(Set<InternalTransaction> internalTransactions) {
        this.setRights(internalTransactions);
        return this;
    }

    public InternalTransaction addRight(InternalTransaction internalTransaction) {
        this.rights.add(internalTransaction);
        internalTransaction.getLefts().add(this);
        return this;
    }

    public InternalTransaction removeRight(InternalTransaction internalTransaction) {
        this.rights.remove(internalTransaction);
        internalTransaction.getLefts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalTransaction)) {
            return false;
        }
        return id != null && id.equals(((InternalTransaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InternalTransaction{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", bookingDate='" + getBookingDate() + "'" +
            ", valueDate='" + getValueDate() + "'" +
            ", value=" + getValue() +
            ", balanceAfter=" + getBalanceAfter() +
            ", description='" + getDescription() + "'" +
            ", serviceQulifier='" + getServiceQulifier() + "'" +
            ", issuer='" + getIssuer() + "'" +
            ", recipient='" + getRecipient() + "'" +
            "}";
    }
}
