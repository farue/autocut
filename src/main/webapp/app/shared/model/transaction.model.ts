import { Moment } from 'moment';
import { ILease } from 'app/shared/model/lease.model';
import { TransactionKind } from 'app/shared/model/enumerations/transaction-kind.model';

export interface ITransaction {
  id?: number;
  kind?: TransactionKind;
  bookingDate?: Moment;
  valueDate?: Moment;
  value?: number;
  balanceAfter?: number;
  description?: string;
  issuer?: string;
  recipient?: string;
  amount?: number;
  balance?: number;
  lease?: ILease;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public kind?: TransactionKind,
    public bookingDate?: Moment,
    public valueDate?: Moment,
    public value?: number,
    public balanceAfter?: number,
    public description?: string,
    public issuer?: string,
    public recipient?: string,
    public amount?: number,
    public balance?: number,
    public lease?: ILease
  ) {}
}
