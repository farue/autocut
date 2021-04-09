import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { APPLICATION_ROUTE } from 'app/info/application/application.route';
import { ApplicationComponent } from 'app/info/application/application.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([APPLICATION_ROUTE])],
  declarations: [ApplicationComponent],
})
export class ApplicationModule {}
