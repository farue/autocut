import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { InfoComponent } from './info.component';
import { APPLICATION_ROUTE } from './application/application.route';
import { REGISTRATION_ROUTE } from './registration/registration.route';
import { FEE_ROUTE } from './fee/fee.route';
import { PROTOCOLS_ROUTE } from './protocols/protocols.route';
import { CONTACTS_ROUTE } from './contacts/contacts.route';

export const INFO_ROUTE: Route = {
  path: 'info',
  component: InfoComponent,
  data: {
    authorities: [],
    pageTitle: 'info.title'
  },
  canActivate: [UserRouteAccessService],
  children: [APPLICATION_ROUTE, REGISTRATION_ROUTE, FEE_ROUTE, PROTOCOLS_ROUTE, CONTACTS_ROUTE]
};
