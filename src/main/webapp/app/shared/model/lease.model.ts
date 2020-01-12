import { Moment } from 'moment';
import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { IApartment } from 'app/shared/model/apartment.model';

export interface ILease {
  id?: number;
  nr?: string;
  start?: Moment;
  end?: Moment;
  createdBy?: string;
  createdDate?: Moment;
  lastModifiedBy?: string;
  lastModifiedDate?: Moment;
  account?: IPaymentAccount;
  tenants?: ITenant[];
  apartment?: IApartment;
}

export class Lease implements ILease {
  constructor(
    public id?: number,
    public nr?: string,
    public start?: Moment,
    public end?: Moment,
    public createdBy?: string,
    public createdDate?: Moment,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Moment,
    public account?: IPaymentAccount,
    public tenants?: ITenant[],
    public apartment?: IApartment
  ) {}
}
