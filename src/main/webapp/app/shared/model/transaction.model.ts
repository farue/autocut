import { Moment } from 'moment';
import { ITransactionBook } from 'app/shared/model/transaction-book.model';

export interface ITransaction {
  id?: number;
  type?: string;
  bookingDate?: Moment;
  valueDate?: Moment;
  value?: number;
  balanceAfter?: number;
  description?: string;
  serviceQulifier?: string;
  lefts?: ITransaction[];
  transactionBook?: ITransactionBook;
  rights?: ITransaction[];
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public type?: string,
    public bookingDate?: Moment,
    public valueDate?: Moment,
    public value?: number,
    public balanceAfter?: number,
    public description?: string,
    public serviceQulifier?: string,
    public lefts?: ITransaction[],
    public transactionBook?: ITransactionBook,
    public rights?: ITransaction[]
  ) {}
}
