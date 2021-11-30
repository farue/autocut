import { Routes } from '@angular/router';

import { ContactComponent } from './contact.component';
import { ContactsEnum } from 'app/contact/contacts.enum';
import { ContactFormComponent } from 'app/contact/contact-form/contact-form.component';

export const CONTACTS_ROUTE: Routes = [
  {
    path: '',
    component: ContactComponent,
    data: {
      pageTitle: 'contact.title',
    },
    pathMatch: 'full',
  },
  {
    path: ContactsEnum.SPOKESPERSON,
    component: ContactFormComponent,
  },
  {
    path: ContactsEnum.ASSIGNMENT_TEAM,
    component: ContactFormComponent,
  },
  {
    path: ContactsEnum.NETWORKING_TEAM,
    component: ContactFormComponent,
  },
  {
    path: ContactsEnum.WASHING_TEAM,
    component: ContactFormComponent,
  },
  {
    path: ContactsEnum.TOOLS_TEAM,
    component: ContactFormComponent,
  },
  {
    path: ContactsEnum.JANITOR,
    component: ContactFormComponent,
  },
];
