import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NetworkSwitchStatusComponent } from './list/network-switch-status.component';
import { NetworkSwitchStatusDetailComponent } from './detail/network-switch-status-detail.component';
import { NetworkSwitchStatusUpdateComponent } from './update/network-switch-status-update.component';
import { NetworkSwitchStatusDeleteDialogComponent } from './delete/network-switch-status-delete-dialog.component';
import { NetworkSwitchStatusRoutingModule } from './route/network-switch-status-routing.module';

@NgModule({
  imports: [SharedModule, NetworkSwitchStatusRoutingModule],
  declarations: [
    NetworkSwitchStatusComponent,
    NetworkSwitchStatusDetailComponent,
    NetworkSwitchStatusUpdateComponent,
    NetworkSwitchStatusDeleteDialogComponent,
  ],
  entryComponents: [NetworkSwitchStatusDeleteDialogComponent],
})
export class NetworkSwitchStatusModule {}
