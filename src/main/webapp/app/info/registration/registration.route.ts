import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { RegistrationComponent } from './registration.component';

export const REGISTRATION_ROUTE: Route = {
  path: 'registration',
  component: RegistrationComponent,
  data: {
    authorities: [],
    pageTitle: 'registration.title'
  },
  canActivate: [UserRouteAccessService]
};
