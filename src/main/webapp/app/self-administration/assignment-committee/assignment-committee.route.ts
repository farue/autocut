import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core';
import { AssignmentCommitteeComponent } from './assignment-committee.component';

export const ASSIGNMENT_COMMITTEE_ROUTE: Route = {
  path: 'assignment-committee',
  component: AssignmentCommitteeComponent,
  data: {
    authorities: [],
    pageTitle: 'assignment-committee.title'
  },
  canActivate: [UserRouteAccessService]
};
