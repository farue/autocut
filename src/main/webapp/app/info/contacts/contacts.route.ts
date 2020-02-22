import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ContactsComponent } from './contacts.component';

export const CONTACTS_ROUTE: Route = {
  path: 'contacts',
  component: ContactsComponent,
  data: {
    authorities: [],
    pageTitle: 'contacts.title'
  },
  canActivate: [UserRouteAccessService]
};
