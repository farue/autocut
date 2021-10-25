import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LaundryProgramComponent } from './list/laundry-program.component';
import { LaundryProgramDetailComponent } from './detail/laundry-program-detail.component';
import { LaundryProgramUpdateComponent } from './update/laundry-program-update.component';
import { LaundryProgramDeleteDialogComponent } from './delete/laundry-program-delete-dialog.component';
import { LaundryProgramRoutingModule } from './route/laundry-program-routing.module';

@NgModule({
  imports: [SharedModule, LaundryProgramRoutingModule],
  declarations: [
    LaundryProgramComponent,
    LaundryProgramDetailComponent,
    LaundryProgramUpdateComponent,
    LaundryProgramDeleteDialogComponent,
  ],
  entryComponents: [LaundryProgramDeleteDialogComponent],
})
export class LaundryProgramModule {}
