import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';
import { ITransaction } from 'app/shared/model/transaction.model';
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
  tenants?: ITenant[];
  accounts?: ITransaction[];
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
    public tenants?: ITenant[],
    public accounts?: ITransaction[],
    public apartment?: IApartment
  ) {}
}
