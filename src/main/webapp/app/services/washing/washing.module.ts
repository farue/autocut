import { NgModule } from '@angular/core';
import { AutocutSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { WashingComponent } from 'app/services/washing/washing.component';
import { WASHING_ROUTE } from 'app/services/washing/washing.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([WASHING_ROUTE])],
  declarations: [WashingComponent]
})
export class WashingModule {}
