import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TenantCommunicationComponent } from './tenant-communication.component';
import { TenantCommunicationDetailComponent } from './tenant-communication-detail.component';
import { TenantCommunicationUpdateComponent } from './tenant-communication-update.component';
import { TenantCommunicationDeleteDialogComponent } from './tenant-communication-delete-dialog.component';
import { tenantCommunicationRoute } from './tenant-communication.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(tenantCommunicationRoute)],
  declarations: [
    TenantCommunicationComponent,
    TenantCommunicationDetailComponent,
    TenantCommunicationUpdateComponent,
    TenantCommunicationDeleteDialogComponent
  ],
  entryComponents: [TenantCommunicationDeleteDialogComponent]
})
export class AutocutTenantCommunicationModule {}
