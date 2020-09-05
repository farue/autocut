import { NgModule } from '@angular/core';
import { AutocutSharedModule } from '../../shared/shared.module';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { TransactionComponent } from './transaction.component';
import { TRANSACTION_ROUTE } from './transaction.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([TRANSACTION_ROUTE]), NgSelectModule, FormsModule],
  declarations: [TransactionComponent],
})
export class TransactionModule {}
