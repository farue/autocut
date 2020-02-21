import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { WashingTeamComponent } from './washing-team.component';

export const WASH_TEAM_ROUTE: Route = {
  path: 'washing',
  component: WashingTeamComponent,
  data: {
    authorities: [],
    pageTitle: 'washing-team.title'
  },
  canActivate: [UserRouteAccessService]
};
