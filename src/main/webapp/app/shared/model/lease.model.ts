import { Moment } from 'moment';
import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { IApartment } from 'app/shared/model/apartment.model';

export interface ILease {
  id?: number;
  start?: Moment;
  end?: Moment;
  account?: IPaymentAccount;
  tenants?: ITenant[];
  apartment?: IApartment;
}

export class Lease implements ILease {
  constructor(
    public id?: number,
    public start?: Moment,
    public end?: Moment,
    public account?: IPaymentAccount,
    public tenants?: ITenant[],
    public apartment?: IApartment
  ) {}
}
