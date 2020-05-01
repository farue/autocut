import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { AssignmentComponent } from 'app/info/statutes/assignment/assignment.component';

export const ASSIGNMENT_ROUTE: Route = {
  path: 'assignment',
  component: AssignmentComponent,
  data: {
    authorities: [],
    pageTitle: 'statutes.assignment.title'
  },
  canActivate: [UserRouteAccessService]
};
