import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { CONTACTS_ROUTE } from 'app/contact/contact.route';
import { ContactComponent } from 'app/contact/contact.component';
import { ContactFormComponent } from './contact-form/contact-form.component';
import { ContactJanitorComponent } from './contact-janitor/contact-janitor.component';
import { UiModule } from '../ui/ui.module';

@NgModule({
  imports: [SharedModule, RouterModule.forChild(CONTACTS_ROUTE), UiModule],
  declarations: [ContactComponent, ContactFormComponent, ContactJanitorComponent],
})
export class ContactModule {}
