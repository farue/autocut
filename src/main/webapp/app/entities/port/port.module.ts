import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { PortComponent } from './port.component';
import { PortDetailComponent } from './port-detail.component';
import { PortUpdateComponent } from './port-update.component';
import { PortDeleteDialogComponent, PortDeletePopupComponent } from './port-delete-dialog.component';
import { portPopupRoute, portRoute } from './port.route';

const ENTITY_STATES = [...portRoute, ...portPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [PortComponent, PortDetailComponent, PortUpdateComponent, PortDeleteDialogComponent, PortDeletePopupComponent],
  entryComponents: [PortDeleteDialogComponent]
})
export class AutocutPortModule {}
