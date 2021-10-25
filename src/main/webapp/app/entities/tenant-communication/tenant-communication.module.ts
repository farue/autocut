import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TenantCommunicationComponent } from './list/tenant-communication.component';
import { TenantCommunicationDetailComponent } from './detail/tenant-communication-detail.component';
import { TenantCommunicationUpdateComponent } from './update/tenant-communication-update.component';
import { TenantCommunicationDeleteDialogComponent } from './delete/tenant-communication-delete-dialog.component';
import { TenantCommunicationRoutingModule } from './route/tenant-communication-routing.module';

@NgModule({
  imports: [SharedModule, TenantCommunicationRoutingModule],
  declarations: [
    TenantCommunicationComponent,
    TenantCommunicationDetailComponent,
    TenantCommunicationUpdateComponent,
    TenantCommunicationDeleteDialogComponent,
  ],
  entryComponents: [TenantCommunicationDeleteDialogComponent],
})
export class TenantCommunicationModule {}
