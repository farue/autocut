import * as dayjs from 'dayjs';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';

export interface IInternalTransaction {
  id?: number;
  type?: string;
  bookingDate?: dayjs.Dayjs;
  valueDate?: dayjs.Dayjs;
  value?: number;
  balanceAfter?: number;
  description?: string | null;
  serviceQulifier?: string | null;
  issuer?: string;
  recipient?: string | null;
  lefts?: IInternalTransaction[] | null;
  transactionBook?: ITransactionBook;
  rights?: IInternalTransaction[] | null;
}

export class InternalTransaction implements IInternalTransaction {
  constructor(
    public id?: number,
    public type?: string,
    public bookingDate?: dayjs.Dayjs,
    public valueDate?: dayjs.Dayjs,
    public value?: number,
    public balanceAfter?: number,
    public description?: string | null,
    public serviceQulifier?: string | null,
    public issuer?: string,
    public recipient?: string | null,
    public lefts?: IInternalTransaction[] | null,
    public transactionBook?: ITransactionBook,
    public rights?: IInternalTransaction[] | null
  ) {}
}

export function getInternalTransactionIdentifier(internalTransaction: IInternalTransaction): number | undefined {
  return internalTransaction.id;
}
