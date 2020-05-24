import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { NetworkSwitchComponent } from './network-switch.component';
import { NetworkSwitchDetailComponent } from './network-switch-detail.component';
import { NetworkSwitchUpdateComponent } from './network-switch-update.component';
import { NetworkSwitchDeleteDialogComponent } from './network-switch-delete-dialog.component';
import { networkSwitchRoute } from './network-switch.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(networkSwitchRoute)],
  declarations: [NetworkSwitchComponent, NetworkSwitchDetailComponent, NetworkSwitchUpdateComponent, NetworkSwitchDeleteDialogComponent],
  entryComponents: [NetworkSwitchDeleteDialogComponent],
})
export class AutocutNetworkSwitchModule {}
