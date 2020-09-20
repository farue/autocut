import { ILease } from 'app/shared/model/lease.model';

export interface ITransactionBook {
  id?: number;
  leases?: ILease[];
}

export class TransactionBook implements ITransactionBook {
  constructor(public id?: number, public leases?: ILease[]) {}
}
