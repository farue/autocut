import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ProtocolsComponent } from './protocols.component';
import { Authority } from 'app/shared/constants/authority.constants';

export const PROTOCOLS_ROUTE: Route = {
  path: '',
  component: ProtocolsComponent,
  data: {
    authorities: [Authority.USER],
    pageTitle: 'protocols.title'
  },
  canActivate: [UserRouteAccessService]
};
