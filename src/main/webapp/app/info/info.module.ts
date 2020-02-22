import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from '../shared/shared.module';

import { INFO_ROUTE, InfoComponent } from './';
import { ApplicationComponent } from './application/application.component';
import { RegistrationComponent } from './registration/registration.component';
import { FeeComponent } from './fee/fee.component';
import { ProtocolsComponent } from './protocols/protocols.component';
import { ContactsComponent } from './contacts/contacts.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forRoot([INFO_ROUTE], { useHash: true })],
  declarations: [InfoComponent, ApplicationComponent, RegistrationComponent, FeeComponent, ProtocolsComponent, ContactsComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AutocutAppInfoModule {}
