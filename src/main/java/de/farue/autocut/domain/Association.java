package de.farue.autocut.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Association.
 */
@Entity
@Table(name = "association")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Association implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", unique = true)
    private Boolean active;

    @JsonIgnoreProperties(value = { "iTransactions", "bTransactions", "leases" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private TransactionBook cashTransactionBook;

    @JsonIgnoreProperties(value = { "iTransactions", "bTransactions", "leases" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private TransactionBook revenueTransactionBook;

    @OneToOne
    @JoinColumn(unique = true)
    private BankAccount bankAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Association id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Association name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Association active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public TransactionBook getCashTransactionBook() {
        return this.cashTransactionBook;
    }

    public void setCashTransactionBook(TransactionBook transactionBook) {
        this.cashTransactionBook = transactionBook;
    }

    public Association cashTransactionBook(TransactionBook transactionBook) {
        this.setCashTransactionBook(transactionBook);
        return this;
    }

    public TransactionBook getRevenueTransactionBook() {
        return this.revenueTransactionBook;
    }

    public void setRevenueTransactionBook(TransactionBook transactionBook) {
        this.revenueTransactionBook = transactionBook;
    }

    public Association revenueTransactionBook(TransactionBook transactionBook) {
        this.setRevenueTransactionBook(transactionBook);
        return this;
    }

    public BankAccount getBankAccount() {
        return this.bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Association bankAccount(BankAccount bankAccount) {
        this.setBankAccount(bankAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Association)) {
            return false;
        }
        return id != null && id.equals(((Association) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Association{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
