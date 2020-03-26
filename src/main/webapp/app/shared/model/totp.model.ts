import { ITenant } from 'app/shared/model/tenant.model';

export interface ITotp {
  id?: number;
  secret?: string;
  tenant?: ITenant;
}

export class Totp implements ITotp {
  constructor(public id?: number, public secret?: string, public tenant?: ITenant) {}
}
