import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core';
import { NetworkTeamComponent } from './network-team.component';

export const NETWORK_TEAM_ROUTE: Route = {
  path: 'network-team',
  component: NetworkTeamComponent,
  data: {
    authorities: [],
    pageTitle: 'network-team.title'
  },
  canActivate: [UserRouteAccessService]
};
