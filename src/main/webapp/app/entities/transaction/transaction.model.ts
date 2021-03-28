import * as dayjs from 'dayjs';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';

export interface ITransaction {
  id?: number;
  type?: string;
  bookingDate?: dayjs.Dayjs;
  valueDate?: dayjs.Dayjs;
  value?: number;
  balanceAfter?: number;
  description?: string | null;
  serviceQulifier?: string | null;
  lefts?: ITransaction[] | null;
  transactionBook?: ITransactionBook;
  rights?: ITransaction[] | null;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public type?: string,
    public bookingDate?: dayjs.Dayjs,
    public valueDate?: dayjs.Dayjs,
    public value?: number,
    public balanceAfter?: number,
    public description?: string | null,
    public serviceQulifier?: string | null,
    public lefts?: ITransaction[] | null,
    public transactionBook?: ITransactionBook,
    public rights?: ITransaction[] | null
  ) {}
}

export function getTransactionIdentifier(transaction: ITransaction): number | undefined {
  return transaction.id;
}
