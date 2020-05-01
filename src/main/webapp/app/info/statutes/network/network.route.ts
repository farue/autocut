import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { NetworkComponent } from './network.component';

export const NETWORK_ROUTE: Route = {
  path: 'network',
  component: NetworkComponent,
  data: {
    authorities: [],
    pageTitle: 'statutes.network.title'
  },
  canActivate: [UserRouteAccessService]
};
