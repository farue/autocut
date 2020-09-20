import { Moment } from 'moment';
import { ITenant } from 'app/shared/model/tenant.model';
import { ITransactionBook } from 'app/shared/model/transaction-book.model';
import { IApartment } from 'app/shared/model/apartment.model';

export interface ILease {
  id?: number;
  nr?: string;
  start?: Moment;
  end?: Moment;
  blocked?: boolean;
  pictureContractContentType?: string;
  pictureContract?: any;
  tenants?: ITenant[];
  transactionBooks?: ITransactionBook[];
  apartment?: IApartment;
}

export class Lease implements ILease {
  constructor(
    public id?: number,
    public nr?: string,
    public start?: Moment,
    public end?: Moment,
    public blocked?: boolean,
    public pictureContractContentType?: string,
    public pictureContract?: any,
    public tenants?: ITenant[],
    public transactionBooks?: ITransactionBook[],
    public apartment?: IApartment
  ) {
    this.blocked = this.blocked || false;
  }
}
