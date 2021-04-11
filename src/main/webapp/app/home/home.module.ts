import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { ServicesOverviewComponent } from './services-overview/services-overview.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent, WelcomeComponent, ServicesOverviewComponent],
})
export class HomeModule {}
