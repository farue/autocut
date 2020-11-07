import { Moment } from 'moment';
import { IBankAccount } from 'app/shared/model/bank-account.model';
import { ITransaction, Transaction } from './transaction.model';
import { ITransactionBook } from './transaction-book.model';

export interface IBankTransaction extends ITransaction {
  customerRef?: string;
  gvCode?: string;
  endToEnd?: string;
  primanota?: string;
  creditor?: string;
  mandate?: string;
  bankAccount?: IBankAccount;
  contraBankAccount?: IBankAccount;
}

export class BankTransaction extends Transaction implements IBankTransaction {
  constructor(
    public id?: number,
    public bookingDate?: Moment,
    public valueDate?: Moment,
    public value?: number,
    public balanceAfter?: number,
    public type?: string,
    public description?: string,
    public serviceQulifier?: string,
    public lefts?: ITransaction[],
    public transactionBook?: ITransactionBook,
    public rights?: ITransaction[],
    public customerRef?: string,
    public gvCode?: string,
    public endToEnd?: string,
    public primanota?: string,
    public creditor?: string,
    public mandate?: string,
    public bankAccount?: IBankAccount,
    public contraBankAccount?: IBankAccount
  ) {
    super(id, type, bookingDate, valueDate, value, balanceAfter, description, serviceQulifier, lefts, transactionBook, rights);
  }
}
