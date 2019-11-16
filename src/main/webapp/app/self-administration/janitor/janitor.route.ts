import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {JanitorComponent} from './janitor.component';

export const JANITOR_ROUTE: Route = {
  path: 'janitor',
  component: JanitorComponent,
  data: {
    authorities: [],
    pageTitle: 'janitor.title'
  },
  canActivate: [UserRouteAccessService]
};
