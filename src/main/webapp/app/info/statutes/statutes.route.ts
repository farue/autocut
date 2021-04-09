import { Route } from '@angular/router';
import { StatutesComponent } from 'app/info/statutes/statutes.component';
import { GENERAL_ROUTE } from 'app/info/statutes/general/general.route';
import { NETWORK_ROUTE } from 'app/info/statutes/network/network.route';
import { ASSIGNMENT_ROUTE } from 'app/info/statutes/assignment/assignment.route';
import { WASHING_ROUTE } from 'app/info/statutes/washing/washing.route';
import { TOOLS_ROUTE } from 'app/info/statutes/tools/tools.route';

export const STATUTES_ROUTE: Route = {
  path: '',
  component: StatutesComponent,
  data: {
    pageTitle: 'statutes.title',
  },
  children: [GENERAL_ROUTE, NETWORK_ROUTE, ASSIGNMENT_ROUTE, WASHING_ROUTE, TOOLS_ROUTE],
};
