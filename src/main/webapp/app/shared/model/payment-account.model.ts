import { IPaymentEntry } from 'app/shared/model/payment-entry.model';
import { ILease } from 'app/shared/model/lease.model';

export interface IPaymentAccount {
  id?: number;
  balance?: number;
  paymentEntries?: IPaymentEntry[];
  lease?: ILease;
}

export class PaymentAccount implements IPaymentAccount {
  constructor(public id?: number, public balance?: number, public paymentEntries?: IPaymentEntry[], public lease?: ILease) {}
}
