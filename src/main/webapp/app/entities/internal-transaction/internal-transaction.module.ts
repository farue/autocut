import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { InternalTransactionComponent } from './list/internal-transaction.component';
import { InternalTransactionDetailComponent } from './detail/internal-transaction-detail.component';
import { InternalTransactionUpdateComponent } from './update/internal-transaction-update.component';
import { InternalTransactionDeleteDialogComponent } from './delete/internal-transaction-delete-dialog.component';
import { InternalTransactionRoutingModule } from './route/internal-transaction-routing.module';

@NgModule({
  imports: [SharedModule, InternalTransactionRoutingModule],
  declarations: [
    InternalTransactionComponent,
    InternalTransactionDetailComponent,
    InternalTransactionUpdateComponent,
    InternalTransactionDeleteDialogComponent,
  ],
  entryComponents: [InternalTransactionDeleteDialogComponent],
})
export class InternalTransactionModule {}
