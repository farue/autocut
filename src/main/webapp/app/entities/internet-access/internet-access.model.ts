import { INetworkSwitch } from 'app/entities/network-switch/network-switch.model';
import { IApartment } from 'app/entities/apartment/apartment.model';

export interface IInternetAccess {
  id?: number;
  blocked?: boolean;
  ip1?: string;
  ip2?: string;
  switchInterface?: string;
  port?: number;
  networkSwitch?: INetworkSwitch | null;
  apartment?: IApartment | null;
}

export class InternetAccess implements IInternetAccess {
  constructor(
    public id?: number,
    public blocked?: boolean,
    public ip1?: string,
    public ip2?: string,
    public switchInterface?: string,
    public port?: number,
    public networkSwitch?: INetworkSwitch | null,
    public apartment?: IApartment | null
  ) {
    this.blocked = this.blocked ?? false;
  }
}

export function getInternetAccessIdentifier(internetAccess: IInternetAccess): number | undefined {
  return internetAccess.id;
}
