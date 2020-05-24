import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { InternetAccessComponent } from './internet-access.component';
import { InternetAccessDetailComponent } from './internet-access-detail.component';
import { InternetAccessUpdateComponent } from './internet-access-update.component';
import { InternetAccessDeleteDialogComponent } from './internet-access-delete-dialog.component';
import { internetAccessRoute } from './internet-access.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(internetAccessRoute)],
  declarations: [
    InternetAccessComponent,
    InternetAccessDetailComponent,
    InternetAccessUpdateComponent,
    InternetAccessDeleteDialogComponent,
  ],
  entryComponents: [InternetAccessDeleteDialogComponent],
})
export class AutocutInternetAccessModule {}
