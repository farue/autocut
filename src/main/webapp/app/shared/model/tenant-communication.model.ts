import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';

export interface ITenantCommunication {
  id?: number;
  subject?: string;
  text?: any;
  note?: any;
  date?: Moment;
  tenant?: ITenant;
}

export class TenantCommunication implements ITenantCommunication {
  constructor(
    public id?: number,
    public subject?: string,
    public text?: any,
    public note?: any,
    public date?: Moment,
    public tenant?: ITenant
  ) {}
}
