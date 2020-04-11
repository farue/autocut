import { NgModule } from '@angular/core';
import { AutocutSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { FEE_ROUTE } from 'app/info/fee/fee.route';
import { FeeComponent } from 'app/info/fee/fee.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([FEE_ROUTE])],
  declarations: [FeeComponent]
})
export class FeeModule {}
