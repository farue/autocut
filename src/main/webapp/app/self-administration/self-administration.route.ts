import {Route} from '@angular/router';

import {UserRouteAccessService} from 'app/core/auth/user-route-access-service';
import {SelfAdministrationComponent} from './self-administration.component';
import {SPOKESMAN_ROUTE} from './spokesman/spokesman.route';
import {ASSIGNMENT_COMMITTEE_ROUTE} from './assignment-committee/assignment-committee.route';
import {JANITOR_ROUTE} from './janitor/janitor.route';

export const SELF_ADMINISTRATION_ROUTE: Route = {
  path: 'self-administration',
  component: SelfAdministrationComponent,
  data: {
    authorities: [],
    pageTitle: 'self-administration.title'
  },
  canActivate: [UserRouteAccessService],
  children: [SPOKESMAN_ROUTE, ASSIGNMENT_COMMITTEE_ROUTE, JANITOR_ROUTE]
};
