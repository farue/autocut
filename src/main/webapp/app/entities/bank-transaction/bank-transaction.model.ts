import dayjs from 'dayjs/esm';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';

export interface IBankTransaction {
  id?: number;
  bookingDate?: dayjs.Dayjs;
  valueDate?: dayjs.Dayjs;
  value?: number;
  balanceAfter?: number;
  type?: string | null;
  description?: string | null;
  customerRef?: string | null;
  gvCode?: string | null;
  endToEnd?: string | null;
  primanota?: string | null;
  creditor?: string | null;
  mandate?: string | null;
  bankAccount?: IBankAccount;
  contraBankAccount?: IBankAccount | null;
  lefts?: IBankTransaction[] | null;
  transactionBook?: ITransactionBook;
  rights?: IBankTransaction[] | null;
}

export class BankTransaction implements IBankTransaction {
  constructor(
    public id?: number,
    public bookingDate?: dayjs.Dayjs,
    public valueDate?: dayjs.Dayjs,
    public value?: number,
    public balanceAfter?: number,
    public type?: string | null,
    public description?: string | null,
    public customerRef?: string | null,
    public gvCode?: string | null,
    public endToEnd?: string | null,
    public primanota?: string | null,
    public creditor?: string | null,
    public mandate?: string | null,
    public bankAccount?: IBankAccount,
    public contraBankAccount?: IBankAccount | null,
    public lefts?: IBankTransaction[] | null,
    public transactionBook?: ITransactionBook,
    public rights?: IBankTransaction[] | null
  ) {}
}

export function getBankTransactionIdentifier(bankTransaction: IBankTransaction): number | undefined {
  return bankTransaction.id;
}
