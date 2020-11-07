import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { InternalTransactionComponent } from './internal-transaction.component';
import { InternalTransactionDetailComponent } from './internal-transaction-detail.component';
import { InternalTransactionUpdateComponent } from './internal-transaction-update.component';
import { InternalTransactionDeleteDialogComponent } from './internal-transaction-delete-dialog.component';
import { internalTransactionRoute } from './internal-transaction.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(internalTransactionRoute)],
  declarations: [
    InternalTransactionComponent,
    InternalTransactionDetailComponent,
    InternalTransactionUpdateComponent,
    InternalTransactionDeleteDialogComponent,
  ],
  entryComponents: [InternalTransactionDeleteDialogComponent],
})
export class AutocutInternalTransactionModule {}
