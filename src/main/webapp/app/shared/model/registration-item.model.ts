import { ITenant } from 'app/shared/model/tenant.model';

export interface IRegistrationItem {
  id?: number;
  item?: string;
  contentType?: string;
  content?: string;
  tenant?: ITenant;
}

export class RegistrationItem implements IRegistrationItem {
  constructor(public id?: number, public item?: string, public contentType?: string, public content?: string, public tenant?: ITenant) {}
}
