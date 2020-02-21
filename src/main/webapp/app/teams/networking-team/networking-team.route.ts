import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { NetworkingTeamComponent } from './networking-team.component';

export const NETWORK_TEAM_ROUTE: Route = {
  path: 'networking',
  component: NetworkingTeamComponent,
  data: {
    authorities: [],
    pageTitle: 'networking-team.title'
  },
  canActivate: [UserRouteAccessService]
};
