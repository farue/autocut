import { ITransaction } from 'app/entities/transaction/transaction.model';
import { ILease } from 'app/entities/lease/lease.model';
import { TransactionBookType } from 'app/entities/enumerations/transaction-book-type.model';

export interface ITransactionBook {
  id?: number;
  name?: string | null;
  type?: TransactionBookType;
  transactions?: ITransaction[] | null;
  leases?: ILease[] | null;
}

export class TransactionBook implements ITransactionBook {
  constructor(
    public id?: number,
    public name?: string | null,
    public type?: TransactionBookType,
    public transactions?: ITransaction[] | null,
    public leases?: ILease[] | null
  ) {}
}

export function getTransactionBookIdentifier(transactionBook: ITransactionBook): number | undefined {
  return transactionBook.id;
}
