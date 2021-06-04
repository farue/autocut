import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WashingComponent } from 'app/services/washing/washing.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { UiModule } from 'app/ui/ui.module';
import { WashingLaundryMachinesComponent } from './washing-laundry-machines/washing-laundry-machines.component';

@NgModule({
  imports: [SharedModule, NgSelectModule, FormsModule, UiModule],
  declarations: [WashingComponent, WashingLaundryMachinesComponent],
  exports: [WashingLaundryMachinesComponent],
})
export class WashingModule {}
