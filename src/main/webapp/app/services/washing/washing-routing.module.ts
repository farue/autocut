import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { WASHING_ROUTE } from 'app/services/washing/washing.route';
import { WashingModule } from 'app/services/washing/washing.module';

@NgModule({
  imports: [WashingModule, RouterModule.forChild([WASHING_ROUTE])],
})
export class WashingRoutingModule {}
