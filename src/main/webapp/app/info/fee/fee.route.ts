import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { FeeComponent } from './fee.component';

export const FEE_ROUTE: Route = {
  path: 'fee',
  component: FeeComponent,
  data: {
    authorities: [],
    pageTitle: 'fee.title'
  },
  canActivate: [UserRouteAccessService]
};
