import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { NetworkSwitchStatusComponent } from './network-switch-status.component';
import { NetworkSwitchStatusDetailComponent } from './network-switch-status-detail.component';
import { NetworkSwitchStatusUpdateComponent } from './network-switch-status-update.component';
import { NetworkSwitchStatusDeleteDialogComponent } from './network-switch-status-delete-dialog.component';
import { networkSwitchStatusRoute } from './network-switch-status.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(networkSwitchStatusRoute)],
  declarations: [
    NetworkSwitchStatusComponent,
    NetworkSwitchStatusDetailComponent,
    NetworkSwitchStatusUpdateComponent,
    NetworkSwitchStatusDeleteDialogComponent,
  ],
  entryComponents: [NetworkSwitchStatusDeleteDialogComponent],
})
export class AutocutNetworkSwitchStatusModule {}
