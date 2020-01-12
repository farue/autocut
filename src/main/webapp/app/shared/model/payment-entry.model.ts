import { Moment } from 'moment';
import { ITransaction } from 'app/shared/model/transaction.model';
import { IPaymentAccount } from 'app/shared/model/payment-account.model';

export interface IPaymentEntry {
  id?: number;
  balanceBefore?: number;
  balanceAfter?: number;
  payment?: number;
  date?: Moment;
  description?: string;
  transaction?: ITransaction;
  account?: IPaymentAccount;
}

export class PaymentEntry implements IPaymentEntry {
  constructor(
    public id?: number,
    public balanceBefore?: number,
    public balanceAfter?: number,
    public payment?: number,
    public date?: Moment,
    public description?: string,
    public transaction?: ITransaction,
    public account?: IPaymentAccount
  ) {}
}
