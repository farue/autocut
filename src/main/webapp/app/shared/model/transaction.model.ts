import { Moment } from 'moment';
import { ITransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionKind } from 'app/shared/model/enumerations/transaction-kind.model';

export interface ITransaction {
  id?: number;
  kind?: TransactionKind;
  bookingDate?: Moment;
  valueDate?: Moment;
  value?: number;
  balanceAfter?: number;
  description?: string;
  serviceQulifier?: string;
  issuer?: string;
  recipient?: string;
  lefts?: ITransaction[];
  transactionBook?: ITransactionBook;
  rights?: ITransaction[];
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public kind?: TransactionKind,
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
  ) {}
}
