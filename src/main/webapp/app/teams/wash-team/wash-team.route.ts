import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {WashTeamComponent} from './wash-team.component';

export const WASH_TEAM_ROUTE: Route = {
  path: 'wash-team',
  component: WashTeamComponent,
  data: {
    authorities: [],
    pageTitle: 'wash-team.title'
  },
  canActivate: [UserRouteAccessService]
};
