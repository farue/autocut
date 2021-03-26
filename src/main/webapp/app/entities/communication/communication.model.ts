import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';

export interface ICommunication {
  id?: number;
  subject?: string;
  text?: string;
  note?: string | null;
  date?: dayjs.Dayjs;
  tenant?: IUser | null;
}

export class Communication implements ICommunication {
  constructor(
    public id?: number,
    public subject?: string,
    public text?: string,
    public note?: string | null,
    public date?: dayjs.Dayjs,
    public tenant?: IUser | null
  ) {}
}

export function getCommunicationIdentifier(communication: ICommunication): number | undefined {
  return communication.id;
}
