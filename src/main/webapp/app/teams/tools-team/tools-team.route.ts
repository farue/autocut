import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ToolsTeamComponent } from './tools-team.component';

export const TOOLS_TEAM_ROUTE: Route = {
  path: 'tools',
  component: ToolsTeamComponent,
  data: {
    authorities: [],
    pageTitle: 'tools-team.title'
  },
  canActivate: [UserRouteAccessService]
};
