import { Route } from '@angular/router';

import { ContactsComponent } from './contacts.component';

export const CONTACTS_ROUTE: Route = {
  path: '',
  component: ContactsComponent,
  data: {
    pageTitle: 'contacts.title'
  }
};
