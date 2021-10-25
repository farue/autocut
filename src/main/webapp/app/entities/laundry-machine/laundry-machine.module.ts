import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LaundryMachineComponent } from './list/laundry-machine.component';
import { LaundryMachineDetailComponent } from './detail/laundry-machine-detail.component';
import { LaundryMachineUpdateComponent } from './update/laundry-machine-update.component';
import { LaundryMachineDeleteDialogComponent } from './delete/laundry-machine-delete-dialog.component';
import { LaundryMachineRoutingModule } from './route/laundry-machine-routing.module';

@NgModule({
  imports: [SharedModule, LaundryMachineRoutingModule],
  declarations: [
    LaundryMachineComponent,
    LaundryMachineDetailComponent,
    LaundryMachineUpdateComponent,
    LaundryMachineDeleteDialogComponent,
  ],
  entryComponents: [LaundryMachineDeleteDialogComponent],
})
export class LaundryMachineModule {}
