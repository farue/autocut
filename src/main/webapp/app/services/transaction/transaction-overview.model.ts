import { ITransaction } from '../../shared/model/transaction.model';

export interface ITransactionOverview {
  balanceNow?: number;
  transactions?: ITransaction[];
}

export class TransactionOverview implements ITransactionOverview {
  constructor(public balanceNow?: number, public transactions?: ITransaction[]) {}
}
