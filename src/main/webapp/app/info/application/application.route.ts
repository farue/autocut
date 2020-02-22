import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ApplicationComponent } from './application.component';

export const APPLICATION_ROUTE: Route = {
  path: 'application',
  component: ApplicationComponent,
  data: {
    authorities: [],
    pageTitle: 'application.title'
  },
  canActivate: [UserRouteAccessService]
};
