import { ITransaction, Transaction } from './transaction.model';
import { Moment } from 'moment';
import { ITransactionBook } from './transaction-book.model';

export interface IInternalTransaction extends ITransaction {
  issuer?: string;
  recipient?: string;
}

export class InternalTransaction extends Transaction implements IInternalTransaction {
  constructor(
    public id?: number,
    public type?: string,
    public bookingDate?: Moment,
    public valueDate?: Moment,
    public value?: number,
    public balanceAfter?: number,
    public description?: string,
    public serviceQulifier?: string,
    public issuer?: string,
    public recipient?: string,
    public lefts?: ITransaction[],
    public transactionBook?: ITransactionBook,
    public rights?: ITransaction[]
  ) {
    super(id, type, bookingDate, valueDate, value, balanceAfter, description, serviceQulifier, lefts, transactionBook, rights);
  }
}
