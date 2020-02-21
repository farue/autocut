import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { TeamsComponent } from './teams.component';
import { NETWORK_TEAM_ROUTE } from './network-team/network-team.route';
import { WASH_TEAM_ROUTE } from './washing-team/washing-team.route';
import { TOOLS_TEAM_ROUTE } from './tools-team/tools-team.route';

export const TEAMS_ROUTE: Route = {
  path: 'teams',
  component: TeamsComponent,
  data: {
    authorities: [],
    pageTitle: 'teams.title'
  },
  canActivate: [UserRouteAccessService],
  children: [NETWORK_TEAM_ROUTE, WASH_TEAM_ROUTE, TOOLS_TEAM_ROUTE]
};
