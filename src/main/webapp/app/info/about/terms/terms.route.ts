import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { TermsComponent } from './terms.component';

export const TERMS_ROUTE: Route = {
  path: 'terms',
  component: TermsComponent,
  data: {
    authorities: [],
    pageTitle: 'about.terms.title'
  },
  canActivate: [UserRouteAccessService]
};
