import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { GeneralComponent } from './general.component';

export const GENERAL_ROUTE: Route = {
  path: 'general',
  component: GeneralComponent,
  data: {
    authorities: [],
    pageTitle: 'statutes.general.title'
  },
  canActivate: [UserRouteAccessService]
};
