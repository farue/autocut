import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ProtocolsComponent } from './protocols.component';

export const PROTOCOLS_ROUTE: Route = {
  path: 'protocols',
  component: ProtocolsComponent,
  data: {
    authorities: [],
    pageTitle: 'protocols.title'
  },
  canActivate: [UserRouteAccessService]
};
