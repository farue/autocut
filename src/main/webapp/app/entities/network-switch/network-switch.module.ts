import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { NetworkSwitchComponent } from './network-switch.component';
import { NetworkSwitchDetailComponent } from './network-switch-detail.component';
import { NetworkSwitchUpdateComponent } from './network-switch-update.component';
import {
  NetworkSwitchDeleteDialogComponent,
  NetworkSwitchDeletePopupComponent
} from './network-switch-delete-dialog.component';
import { networkSwitchPopupRoute, networkSwitchRoute } from './network-switch.route';

const ENTITY_STATES = [...networkSwitchRoute, ...networkSwitchPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    NetworkSwitchComponent,
    NetworkSwitchDetailComponent,
    NetworkSwitchUpdateComponent,
    NetworkSwitchDeleteDialogComponent,
    NetworkSwitchDeletePopupComponent
  ],
  entryComponents: [NetworkSwitchDeleteDialogComponent]
})
export class AutocutNetworkSwitchModule {}
