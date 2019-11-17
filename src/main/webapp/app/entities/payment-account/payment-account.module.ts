import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { PaymentAccountComponent } from './payment-account.component';
import { PaymentAccountDetailComponent } from './payment-account-detail.component';
import { PaymentAccountUpdateComponent } from './payment-account-update.component';
import {
  PaymentAccountDeleteDialogComponent,
  PaymentAccountDeletePopupComponent
} from './payment-account-delete-dialog.component';
import { paymentAccountPopupRoute, paymentAccountRoute } from './payment-account.route';

const ENTITY_STATES = [...paymentAccountRoute, ...paymentAccountPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    PaymentAccountComponent,
    PaymentAccountDetailComponent,
    PaymentAccountUpdateComponent,
    PaymentAccountDeleteDialogComponent,
    PaymentAccountDeletePopupComponent
  ],
  entryComponents: [PaymentAccountDeleteDialogComponent]
})
export class AutocutPaymentAccountModule {}
