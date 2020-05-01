import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { WashingComponent } from './washing.component';

export const WASHING_ROUTE: Route = {
  path: 'washing',
  component: WashingComponent,
  data: {
    authorities: [],
    pageTitle: 'washing.title'
  },
  canActivate: [UserRouteAccessService]
};
