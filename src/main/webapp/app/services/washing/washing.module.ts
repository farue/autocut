import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { WashingComponent } from 'app/services/washing/washing.component';
import { WASHING_ROUTE } from 'app/services/washing/washing.route';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { UiModule } from 'app/ui/ui.module';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([WASHING_ROUTE]), NgSelectModule, FormsModule, UiModule],
  declarations: [WashingComponent],
})
export class WashingModule {}
