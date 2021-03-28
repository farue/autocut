import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { CONTACTS_ROUTE } from 'app/info/contacts/contacts.route';
import { ContactsComponent } from 'app/info/contacts/contacts.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([CONTACTS_ROUTE])],
  declarations: [ContactsComponent]
})
export class ContactsModule {}
