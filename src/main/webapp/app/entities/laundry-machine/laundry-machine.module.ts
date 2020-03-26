import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { LaundryMachineComponent } from './laundry-machine.component';
import { LaundryMachineDetailComponent } from './laundry-machine-detail.component';
import { LaundryMachineUpdateComponent } from './laundry-machine-update.component';
import { LaundryMachineDeleteDialogComponent } from './laundry-machine-delete-dialog.component';
import { laundryMachineRoute } from './laundry-machine.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(laundryMachineRoute)],
  declarations: [
    LaundryMachineComponent,
    LaundryMachineDetailComponent,
    LaundryMachineUpdateComponent,
    LaundryMachineDeleteDialogComponent
  ],
  entryComponents: [LaundryMachineDeleteDialogComponent]
})
export class AutocutLaundryMachineModule {}
