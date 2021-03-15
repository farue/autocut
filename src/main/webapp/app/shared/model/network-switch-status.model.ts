import { Moment } from 'moment';
import { INetworkSwitch } from 'app/shared/model/network-switch.model';

export interface INetworkSwitchStatus {
  id?: number;
  port?: string;
  name?: string;
  status?: string;
  vlan?: string;
  speed?: string;
  type?: string;
  timestamp?: Moment;
  networkSwitch?: INetworkSwitch;
}

export class NetworkSwitchStatus implements INetworkSwitchStatus {
  constructor(
    public id?: number,
    public port?: string,
    public name?: string,
    public status?: string,
    public vlan?: string,
    public speed?: string,
    public type?: string,
    public timestamp?: Moment,
    public networkSwitch?: INetworkSwitch
  ) {}
}
