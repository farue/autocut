import { Moment } from 'moment';
import { ILease } from 'app/shared/model/lease.model';

export interface ICoin {
  id?: number;
  token?: string;
  datePurchase?: Moment;
  dateRedeem?: Moment;
  tenant?: ILease;
}

export class Coin implements ICoin {
  constructor(
    public id?: number,
    public token?: string,
    public datePurchase?: Moment,
    public dateRedeem?: Moment,
    public tenant?: ILease
  ) {}
}
