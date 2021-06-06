import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { InternetComponent } from './internet.component';
import { InternetStatusComponent } from './internet-status/internet-status.component';
import { UiModule } from 'app/ui/ui.module';

@NgModule({
  imports: [SharedModule, NgSelectModule, UiModule],
  declarations: [InternetComponent, InternetStatusComponent],
  exports: [InternetStatusComponent],
})
export class InternetModule {}
