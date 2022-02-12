import dayjs from 'dayjs/esm';
import { ITenant } from 'app/entities/tenant/tenant.model';

export interface ITenantCommunication {
  id?: number;
  subject?: string;
  text?: string;
  note?: string | null;
  date?: dayjs.Dayjs;
  tenant?: ITenant | null;
}

export class TenantCommunication implements ITenantCommunication {
  constructor(
    public id?: number,
    public subject?: string,
    public text?: string,
    public note?: string | null,
    public date?: dayjs.Dayjs,
    public tenant?: ITenant | null
  ) {}
}

export function getTenantCommunicationIdentifier(tenantCommunication: ITenantCommunication): number | undefined {
  return tenantCommunication.id;
}
