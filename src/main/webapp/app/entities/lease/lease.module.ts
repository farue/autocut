import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { LeaseComponent } from './lease.component';
import { LeaseDetailComponent } from './lease-detail.component';
import { LeaseUpdateComponent } from './lease-update.component';
import { LeaseDeleteDialogComponent, LeaseDeletePopupComponent } from './lease-delete-dialog.component';
import { leasePopupRoute, leaseRoute } from './lease.route';

const ENTITY_STATES = [...leaseRoute, ...leasePopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [LeaseComponent, LeaseDetailComponent, LeaseUpdateComponent, LeaseDeleteDialogComponent, LeaseDeletePopupComponent],
  entryComponents: [LeaseDeleteDialogComponent]
})
export class AutocutLeaseModule {}
