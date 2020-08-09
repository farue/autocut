import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface ICommunication {
  id?: number;
  subject?: string;
  text?: any;
  note?: any;
  date?: Moment;
  tenant?: IUser;
}

export class Communication implements ICommunication {
  constructor(
    public id?: number,
    public subject?: string,
    public text?: any,
    public note?: any,
    public date?: Moment,
    public tenant?: IUser
  ) {}
}
