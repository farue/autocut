import { ITransaction } from 'app/shared/model/transaction.model';
import { ILease } from 'app/shared/model/lease.model';
import { TransactionBookType } from 'app/shared/model/enumerations/transaction-book-type.model';

export interface ITransactionBook {
  id?: number;
  name?: string;
  type?: TransactionBookType;
  transactions?: ITransaction[];
  leases?: ILease[];
}

export class TransactionBook implements ITransactionBook {
  constructor(
    public id?: number,
    public name?: string,
    public type?: TransactionBookType,
    public transactions?: ITransaction[],
    public leases?: ILease[]
  ) {}
}
