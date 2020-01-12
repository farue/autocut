import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { PaymentEntryComponent } from './payment-entry.component';
import { PaymentEntryDetailComponent } from './payment-entry-detail.component';
import { PaymentEntryUpdateComponent } from './payment-entry-update.component';
import { PaymentEntryDeleteDialogComponent } from './payment-entry-delete-dialog.component';
import { paymentEntryRoute } from './payment-entry.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(paymentEntryRoute)],
  declarations: [PaymentEntryComponent, PaymentEntryDetailComponent, PaymentEntryUpdateComponent, PaymentEntryDeleteDialogComponent],
  entryComponents: [PaymentEntryDeleteDialogComponent]
})
export class AutocutPaymentEntryModule {}
