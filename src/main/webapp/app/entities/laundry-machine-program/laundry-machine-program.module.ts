import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LaundryMachineProgramComponent } from './list/laundry-machine-program.component';
import { LaundryMachineProgramDetailComponent } from './detail/laundry-machine-program-detail.component';
import { LaundryMachineProgramUpdateComponent } from './update/laundry-machine-program-update.component';
import { LaundryMachineProgramDeleteDialogComponent } from './delete/laundry-machine-program-delete-dialog.component';
import { LaundryMachineProgramRoutingModule } from './route/laundry-machine-program-routing.module';

@NgModule({
  imports: [SharedModule, LaundryMachineProgramRoutingModule],
  declarations: [
    LaundryMachineProgramComponent,
    LaundryMachineProgramDetailComponent,
    LaundryMachineProgramUpdateComponent,
    LaundryMachineProgramDeleteDialogComponent,
  ],
  entryComponents: [LaundryMachineProgramDeleteDialogComponent],
})
export class LaundryMachineProgramModule {}
