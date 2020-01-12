import { Moment } from 'moment';
import { TransactionKind } from 'app/shared/model/enumerations/transaction-kind.model';

export interface ITransaction {
  id?: number;
  kind?: TransactionKind;
  bookingDate?: Moment;
  valueDate?: Moment;
  details?: string;
  issuer?: string;
  recipient?: string;
  amount?: number;
  balance?: number;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public kind?: TransactionKind,
    public bookingDate?: Moment,
    public valueDate?: Moment,
    public details?: string,
    public issuer?: string,
    public recipient?: string,
    public amount?: number,
    public balance?: number
  ) {}
}
