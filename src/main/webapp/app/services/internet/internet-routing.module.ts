import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { INTERNET_ROUTE } from './internet.route';
import { InternetModule } from 'app/services/internet/internet.module';

@NgModule({
  imports: [InternetModule, RouterModule.forChild([INTERNET_ROUTE])],
})
export class InternetRoutingModule {}
