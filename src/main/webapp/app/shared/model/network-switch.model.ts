import { IPort } from 'app/shared/model/port.model';

export interface INetworkSwitch {
  id?: number;
  switchInterface?: string;
  ports?: IPort[];
}

export class NetworkSwitch implements INetworkSwitch {
  constructor(public id?: number, public switchInterface?: string, public ports?: IPort[]) {}
}
