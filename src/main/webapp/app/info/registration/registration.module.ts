import { NgModule } from '@angular/core';
import { AutocutSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { REGISTRATION_ROUTE } from 'app/info/registration/registration.route';
import { RegistrationComponent } from 'app/info/registration/registration.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([REGISTRATION_ROUTE])],
  declarations: [RegistrationComponent]
})
export class RegistrationModule {}
