import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TenantCommunicationComponent } from './tenant-communication.component';
import { TenantCommunicationDetailComponent } from './tenant-communication-detail.component';
import { TenantCommunicationUpdateComponent } from './tenant-communication-update.component';
import {
  TenantCommunicationDeleteDialogComponent,
  TenantCommunicationDeletePopupComponent
} from './tenant-communication-delete-dialog.component';
import { tenantCommunicationPopupRoute, tenantCommunicationRoute } from './tenant-communication.route';

const ENTITY_STATES = [...tenantCommunicationRoute, ...tenantCommunicationPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    TenantCommunicationComponent,
    TenantCommunicationDetailComponent,
    TenantCommunicationUpdateComponent,
    TenantCommunicationDeleteDialogComponent,
    TenantCommunicationDeletePopupComponent
  ],
  entryComponents: [TenantCommunicationDeleteDialogComponent]
})
export class AutocutTenantCommunicationModule {}
