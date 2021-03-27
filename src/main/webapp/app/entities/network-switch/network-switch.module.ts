import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { NetworkSwitchComponent } from './list/network-switch.component';
import { NetworkSwitchDetailComponent } from './detail/network-switch-detail.component';
import { NetworkSwitchUpdateComponent } from './update/network-switch-update.component';
import { NetworkSwitchDeleteDialogComponent } from './delete/network-switch-delete-dialog.component';
import { NetworkSwitchRoutingModule } from './route/network-switch-routing.module';

@NgModule({
  imports: [SharedModule, NetworkSwitchRoutingModule],
  declarations: [NetworkSwitchComponent, NetworkSwitchDetailComponent, NetworkSwitchUpdateComponent, NetworkSwitchDeleteDialogComponent],
  entryComponents: [NetworkSwitchDeleteDialogComponent],
})
export class NetworkSwitchModule {}
