import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';

export interface ITenantCommunication {
  id?: number;
  text?: any;
  date?: Moment;
  tenant?: ITenant;
}

export class TenantCommunication implements ITenantCommunication {
  constructor(public id?: number, public text?: any, public date?: Moment, public tenant?: ITenant) {}
}
