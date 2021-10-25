import { IInternalTransaction } from 'app/entities/internal-transaction/internal-transaction.model';
import { IBankTransaction } from 'app/entities/bank-transaction/bank-transaction.model';
import { ILease } from 'app/entities/lease/lease.model';
import { TransactionBookType } from 'app/entities/enumerations/transaction-book-type.model';

export interface ITransactionBook {
  id?: number;
  name?: string | null;
  type?: TransactionBookType;
  iTransactions?: IInternalTransaction[] | null;
  bTransactions?: IBankTransaction[] | null;
  leases?: ILease[] | null;
}

export class TransactionBook implements ITransactionBook {
  constructor(
    public id?: number,
    public name?: string | null,
    public type?: TransactionBookType,
    public iTransactions?: IInternalTransaction[] | null,
    public bTransactions?: IBankTransaction[] | null,
    public leases?: ILease[] | null
  ) {}
}

export function getTransactionBookIdentifier(transactionBook: ITransactionBook): number | undefined {
  return transactionBook.id;
}
