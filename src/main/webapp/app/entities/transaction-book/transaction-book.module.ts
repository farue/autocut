import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {TransactionBookComponent} from './list/transaction-book.component';
import {TransactionBookDetailComponent} from './detail/transaction-book-detail.component';
import {TransactionBookUpdateComponent} from './update/transaction-book-update.component';
import {TransactionBookDeleteDialogComponent} from './delete/transaction-book-delete-dialog.component';
import {TransactionBookRoutingModule} from './route/transaction-book-routing.module';

@NgModule({
  imports: [SharedModule, TransactionBookRoutingModule],
  declarations: [
    TransactionBookComponent,
    TransactionBookDetailComponent,
    TransactionBookUpdateComponent,
    TransactionBookDeleteDialogComponent,
  ],
  entryComponents: [TransactionBookDeleteDialogComponent],
})
export class TransactionBookModule {}
