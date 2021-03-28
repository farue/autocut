import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { FEE_ROUTE } from 'app/info/fee/fee.route';
import { FeeComponent } from 'app/info/fee/fee.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([FEE_ROUTE])],
  declarations: [FeeComponent]
})
export class FeeModule {}
