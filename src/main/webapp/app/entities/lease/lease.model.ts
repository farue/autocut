import * as dayjs from 'dayjs';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { IApartment } from 'app/entities/apartment/apartment.model';

export interface ILease {
  id?: number;
  nr?: string;
  start?: dayjs.Dayjs;
  end?: dayjs.Dayjs;
  blocked?: boolean | null;
  pictureContractContentType?: string | null;
  pictureContract?: string | null;
  tenants?: ITenant[] | null;
  transactionBooks?: ITransactionBook[] | null;
  apartment?: IApartment | null;
}

export class Lease implements ILease {
  constructor(
    public id?: number,
    public nr?: string,
    public start?: dayjs.Dayjs,
    public end?: dayjs.Dayjs,
    public blocked?: boolean | null,
    public pictureContractContentType?: string | null,
    public pictureContract?: string | null,
    public tenants?: ITenant[] | null,
    public transactionBooks?: ITransactionBook[] | null,
    public apartment?: IApartment | null
  ) {
    this.blocked = this.blocked ?? false;
  }
}

export function getLeaseIdentifier(lease: ILease): number | undefined {
  return lease.id;
}
