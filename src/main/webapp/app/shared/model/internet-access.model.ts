import { IPort } from 'app/shared/model/port.model';
import { IApartment } from 'app/shared/model/apartment.model';

export interface IInternetAccess {
  id?: number;
  blocked?: boolean;
  ip1?: string;
  ip2?: string;
  port?: IPort;
  apartment?: IApartment;
}

export class InternetAccess implements IInternetAccess {
  constructor(
    public id?: number,
    public blocked?: boolean,
    public ip1?: string,
    public ip2?: string,
    public port?: IPort,
    public apartment?: IApartment
  ) {
    this.blocked = this.blocked || false;
  }
}
