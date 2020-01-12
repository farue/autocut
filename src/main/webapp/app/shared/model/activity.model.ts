import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';

export interface IActivity {
  id?: number;
  semester?: string;
  date?: Moment;
  description?: string;
  discount?: boolean;
  stwActivity?: boolean;
  tenant?: ITenant;
}

export class Activity implements IActivity {
  constructor(
    public id?: number,
    public semester?: string,
    public date?: Moment,
    public description?: string,
    public discount?: boolean,
    public stwActivity?: boolean,
    public tenant?: ITenant
  ) {
    this.discount = this.discount || false;
    this.stwActivity = this.stwActivity || false;
  }
}
