import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CookiesComponent } from './cookies.component';

export const COOKIES_ROUTE: Route = {
  path: 'cookies',
  component: CookiesComponent,
  data: {
    authorities: [],
    pageTitle: 'about.cookies.title'
  },
  canActivate: [UserRouteAccessService]
};
