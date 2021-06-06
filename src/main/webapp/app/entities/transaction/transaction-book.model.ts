import { TransactionBookType } from 'app/entities/enumerations/transaction-book-type.model';

export interface TransactionBook {
  id: number;
  name?: string;
  type: TransactionBookType;
  balance: number;
}
