import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { RegistrationItemComponent } from './registration-item.component';
import { RegistrationItemDetailComponent } from './registration-item-detail.component';
import { RegistrationItemUpdateComponent } from './registration-item-update.component';
import { RegistrationItemDeleteDialogComponent } from './registration-item-delete-dialog.component';
import { registrationItemRoute } from './registration-item.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(registrationItemRoute)],
  declarations: [
    RegistrationItemComponent,
    RegistrationItemDetailComponent,
    RegistrationItemUpdateComponent,
    RegistrationItemDeleteDialogComponent,
  ],
  entryComponents: [RegistrationItemDeleteDialogComponent],
})
export class AutocutRegistrationItemModule {}
