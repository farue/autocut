import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { InternetAccessComponent } from './list/internet-access.component';
import { InternetAccessDetailComponent } from './detail/internet-access-detail.component';
import { InternetAccessUpdateComponent } from './update/internet-access-update.component';
import { InternetAccessDeleteDialogComponent } from './delete/internet-access-delete-dialog.component';
import { InternetAccessRoutingModule } from './route/internet-access-routing.module';

@NgModule({
  imports: [SharedModule, InternetAccessRoutingModule],
  declarations: [
    InternetAccessComponent,
    InternetAccessDetailComponent,
    InternetAccessUpdateComponent,
    InternetAccessDeleteDialogComponent,
  ],
  entryComponents: [InternetAccessDeleteDialogComponent],
})
export class InternetAccessModule {}
