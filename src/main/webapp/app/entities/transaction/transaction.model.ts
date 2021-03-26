import * as dayjs from 'dayjs';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionKind } from 'app/entities/enumerations/transaction-kind.model';

export interface ITransaction {
  id?: number;
  kind?: TransactionKind;
  bookingDate?: dayjs.Dayjs;
  valueDate?: dayjs.Dayjs;
  value?: number;
  balanceAfter?: number;
  description?: string | null;
  serviceQulifier?: string | null;
  issuer?: string;
  recipient?: string | null;
  lefts?: ITransaction[] | null;
  transactionBook?: ITransactionBook;
  rights?: ITransaction[] | null;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public kind?: TransactionKind,
    public bookingDate?: dayjs.Dayjs,
    public valueDate?: dayjs.Dayjs,
    public value?: number,
    public balanceAfter?: number,
    public description?: string | null,
    public serviceQulifier?: string | null,
    public issuer?: string,
    public recipient?: string | null,
    public lefts?: ITransaction[] | null,
    public transactionBook?: ITransactionBook,
    public rights?: ITransaction[] | null
  ) {}
}

export function getTransactionIdentifier(transaction: ITransaction): number | undefined {
  return transaction.id;
}
