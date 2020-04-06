package de.farue.autocut.domain.enumeration;

/**
 * The TransactionKind enumeration.
 */
public enum TransactionKind {
    /**
     * A transaction with negative amount. The difference between a fee and a purchase is that a
     * fee is not paid in exchange for an immediate good.
     */
    FEE,

    /**
     * A transaction with a positive amount. The amount is credited to the account.
     */
    CREDIT,

    /**
     * A transaction with a negative amount. The amount is withdrawn from the account.
     */
    DEBIT,

    /**
     * A transfer to a different account.
     */
    TRANSFER,

    /**
     * A corrective booking due to cancellation or system malfunctions.
     */
    CORRECTION,

    /**
     * A transaction with a negative amount. In exchange the payer receives a different good.
     */
    PURCHASE
}
