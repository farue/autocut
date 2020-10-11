import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TransactionBookComponent } from './transaction-book.component';
import { TransactionBookDetailComponent } from './transaction-book-detail.component';
import { TransactionBookUpdateComponent } from './transaction-book-update.component';
import { TransactionBookDeleteDialogComponent } from './transaction-book-delete-dialog.component';
import { transactionBookRoute } from './transaction-book.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(transactionBookRoute)],
  declarations: [
    TransactionBookComponent,
    TransactionBookDetailComponent,
    TransactionBookUpdateComponent,
    TransactionBookDeleteDialogComponent,
  ],
  entryComponents: [TransactionBookDeleteDialogComponent],
})
export class AutocutTransactionBookModule {}
