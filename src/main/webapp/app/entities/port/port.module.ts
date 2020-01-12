import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { PortComponent } from './port.component';
import { PortDetailComponent } from './port-detail.component';
import { PortUpdateComponent } from './port-update.component';
import { PortDeleteDialogComponent } from './port-delete-dialog.component';
import { portRoute } from './port.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(portRoute)],
  declarations: [PortComponent, PortDetailComponent, PortUpdateComponent, PortDeleteDialogComponent],
  entryComponents: [PortDeleteDialogComponent]
})
export class AutocutPortModule {}
