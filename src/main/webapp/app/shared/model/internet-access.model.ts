import { IApartment } from 'app/shared/model/apartment.model';

export interface IInternetAccess {
  id?: number;
  blocked?: boolean;
  ip1?: string;
  ip2?: string;
  switchInterface?: string;
  port?: number;
  apartment?: IApartment;
}

export class InternetAccess implements IInternetAccess {
  constructor(
    public id?: number,
    public blocked?: boolean,
    public ip1?: string,
    public ip2?: string,
    public switchInterface?: string,
    public port?: number,
    public apartment?: IApartment
  ) {
    this.blocked = this.blocked || false;
  }
}
