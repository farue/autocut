import { ITransaction } from 'app/shared/model/transaction.model';
import { ILease } from 'app/shared/model/lease.model';

export interface IPaymentAccount {
  id?: number;
  balance?: number;
  transactions?: ITransaction[];
  lease?: ILease;
}

export class PaymentAccount implements IPaymentAccount {
  constructor(public id?: number, public balance?: number, public transactions?: ITransaction[], public lease?: ILease) {}
}
