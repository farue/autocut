import { ITransaction } from '../../shared/model/transaction.model';

export interface ITransactionOverview {
  balanceNow?: number;
  deposit?: number;
  transactions?: ITransaction[];
}

export class TransactionOverview implements ITransactionOverview {
  constructor(public balanceNow?: number, public deposit?: number, public transactions?: ITransaction[]) {}
}
