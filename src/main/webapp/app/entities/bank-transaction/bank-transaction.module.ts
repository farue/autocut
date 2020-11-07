import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { BankTransactionComponent } from './bank-transaction.component';
import { BankTransactionDetailComponent } from './bank-transaction-detail.component';
import { BankTransactionUpdateComponent } from './bank-transaction-update.component';
import { BankTransactionDeleteDialogComponent } from './bank-transaction-delete-dialog.component';
import { bankTransactionRoute } from './bank-transaction.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(bankTransactionRoute)],
  declarations: [
    BankTransactionComponent,
    BankTransactionDetailComponent,
    BankTransactionUpdateComponent,
    BankTransactionDeleteDialogComponent,
  ],
  entryComponents: [BankTransactionDeleteDialogComponent],
})
export class AutocutBankTransactionModule {}
