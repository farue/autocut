import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { INetworkSwitch } from 'app/shared/model/network-switch.model';

export interface IPort {
  id?: number;
  number?: number;
  internetAccess?: IInternetAccess;
  networkSwitch?: INetworkSwitch;
}

export class Port implements IPort {
  constructor(public id?: number, public number?: number, public internetAccess?: IInternetAccess, public networkSwitch?: INetworkSwitch) {}
}
