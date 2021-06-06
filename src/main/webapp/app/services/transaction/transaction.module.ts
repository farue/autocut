import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { TransactionComponent } from './transaction.component';
import { TransactionCellComponent } from './transaction-cell/transaction-cell.component';
import { UiModule } from 'app/ui/ui.module';
import { TransactionBalanceComponent } from './transaction-balance/transaction-balance.component';

@NgModule({
  imports: [SharedModule, NgSelectModule, FormsModule, UiModule],
  declarations: [TransactionComponent, TransactionCellComponent, TransactionBalanceComponent],
  exports: [TransactionBalanceComponent],
})
export class TransactionModule {}
