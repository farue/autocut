import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { TransactionComponent } from './transaction.component';
import { TRANSACTION_ROUTE } from './transaction.route';
import { TransactionCellComponent } from './transaction-cell/transaction-cell.component';
import { UiModule } from 'app/ui/ui.module';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([TRANSACTION_ROUTE]), NgSelectModule, FormsModule, UiModule],
  declarations: [TransactionComponent, TransactionCellComponent],
})
export class TransactionModule {}
