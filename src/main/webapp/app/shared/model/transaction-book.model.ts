import { ILease } from 'app/shared/model/lease.model';
import { TransactionBookType } from 'app/shared/model/enumerations/transaction-book-type.model';

export interface ITransactionBook {
  id?: number;
  name?: string;
  type?: TransactionBookType;
  leases?: ILease[];
}

export class TransactionBook implements ITransactionBook {
  constructor(public id?: number, public name?: string, public type?: TransactionBookType, public leases?: ILease[]) {}
}
