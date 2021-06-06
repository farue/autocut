import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TRANSACTION_ROUTE } from './transaction.route';
import { TransactionModule } from 'app/services/transaction/transaction.module';

@NgModule({
  imports: [TransactionModule, RouterModule.forChild([TRANSACTION_ROUTE])],
})
export class TransactionRoutingModule {}
