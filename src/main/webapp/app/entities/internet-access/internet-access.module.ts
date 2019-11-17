import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { InternetAccessComponent } from './internet-access.component';
import { InternetAccessDetailComponent } from './internet-access-detail.component';
import { InternetAccessUpdateComponent } from './internet-access-update.component';
import {
  InternetAccessDeleteDialogComponent,
  InternetAccessDeletePopupComponent
} from './internet-access-delete-dialog.component';
import { internetAccessPopupRoute, internetAccessRoute } from './internet-access.route';

const ENTITY_STATES = [...internetAccessRoute, ...internetAccessPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    InternetAccessComponent,
    InternetAccessDetailComponent,
    InternetAccessUpdateComponent,
    InternetAccessDeleteDialogComponent,
    InternetAccessDeletePopupComponent
  ],
  entryComponents: [InternetAccessDeleteDialogComponent]
})
export class AutocutInternetAccessModule {}
