package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.farue.autocut.domain.enumeration.TransactionType;

/**
 * A InternalTransaction.
 */
@Entity
@Table(name = "internal_transaction")
public class InternalTransaction extends Transaction {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "recipient")
    private String recipient;

    @Override
    public InternalTransaction bookingDate(Instant bookingDate) {
        super.bookingDate(bookingDate);
        return this;
    }

    @Override
    public InternalTransaction valueDate(Instant valueDate) {
        super.valueDate(valueDate);
        return this;
    }

    @Override
    public InternalTransaction value(BigDecimal value) {
        super.value(value);
        return this;
    }

    @Override
    public InternalTransaction balanceAfter(BigDecimal balanceAfter) {
        super.balanceAfter(balanceAfter);
        return this;
    }

    @Override
    public InternalTransaction type(String type) {
        super.type(type);
        return this;
    }

    @Override
    public InternalTransaction description(String description) {
        super.description(description);
        return this;
    }

    @Override
    public InternalTransaction serviceQulifier(String serviceQulifier) {
        super.serviceQulifier(serviceQulifier);
        return this;
    }

    @Override
    public InternalTransaction transactionBook(TransactionBook transactionBook) {
        super.transactionBook(transactionBook);
        return this;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public InternalTransaction issuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public InternalTransaction recipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @JsonIgnore
    public TransactionType getTransactionType() {
        return TransactionType.valueOf(getType());
    }

    public InternalTransaction transactionType(TransactionType type) {
        super.type(type.name());
        return this;
    }

    public void setTransactionType(TransactionType type) {
        this.setType(type.name());
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
