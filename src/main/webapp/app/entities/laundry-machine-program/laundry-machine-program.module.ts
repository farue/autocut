import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { LaundryMachineProgramComponent } from './laundry-machine-program.component';
import { LaundryMachineProgramDetailComponent } from './laundry-machine-program-detail.component';
import { LaundryMachineProgramUpdateComponent } from './laundry-machine-program-update.component';
import { LaundryMachineProgramDeleteDialogComponent } from './laundry-machine-program-delete-dialog.component';
import { laundryMachineProgramRoute } from './laundry-machine-program.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(laundryMachineProgramRoute)],
  declarations: [
    LaundryMachineProgramComponent,
    LaundryMachineProgramDetailComponent,
    LaundryMachineProgramUpdateComponent,
    LaundryMachineProgramDeleteDialogComponent
  ],
  entryComponents: [LaundryMachineProgramDeleteDialogComponent]
})
export class AutocutLaundryMachineProgramModule {}
