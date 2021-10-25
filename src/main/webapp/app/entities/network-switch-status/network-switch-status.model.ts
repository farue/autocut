import * as dayjs from 'dayjs';
import { INetworkSwitch } from 'app/entities/network-switch/network-switch.model';

export interface INetworkSwitchStatus {
  id?: number;
  port?: string | null;
  name?: string | null;
  status?: string | null;
  vlan?: string | null;
  speed?: string | null;
  type?: string | null;
  timestamp?: dayjs.Dayjs | null;
  networkSwitch?: INetworkSwitch | null;
}

export class NetworkSwitchStatus implements INetworkSwitchStatus {
  constructor(
    public id?: number,
    public port?: string | null,
    public name?: string | null,
    public status?: string | null,
    public vlan?: string | null,
    public speed?: string | null,
    public type?: string | null,
    public timestamp?: dayjs.Dayjs | null,
    public networkSwitch?: INetworkSwitch | null
  ) {}
}

export function getNetworkSwitchStatusIdentifier(networkSwitchStatus: INetworkSwitchStatus): number | undefined {
  return networkSwitchStatus.id;
}
