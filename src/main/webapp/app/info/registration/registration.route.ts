import { Route } from '@angular/router';

import { RegistrationComponent } from './registration.component';

export const REGISTRATION_ROUTE: Route = {
  path: '',
  component: RegistrationComponent,
  data: {
    pageTitle: 'registration.title'
  }
};
