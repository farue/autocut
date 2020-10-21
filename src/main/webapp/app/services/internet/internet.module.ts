import { NgModule } from '@angular/core';
import { AutocutSharedModule } from '../../shared/shared.module';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { InternetComponent } from './internet.component';
import { INTERNET_ROUTE } from './internet.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([INTERNET_ROUTE]), NgSelectModule],
  declarations: [InternetComponent],
})
export class InternetModule {}
