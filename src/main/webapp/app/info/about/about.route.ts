import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AboutComponent } from './about.component';
import { TERMS_ROUTE } from 'app/info/about/terms/terms.route';
import { PRIVACY_ROUTE } from 'app/info/about/privacy/privacy.route';
import { COOKIES_ROUTE } from 'app/info/about/cookies/cookies.route';
import { IMPRINT_ROUTE } from 'app/info/about/imprint/imprint.route';

export const ABOUT_ROUTE: Route = {
  path: '',
  component: AboutComponent,
  data: {
    authorities: [],
    pageTitle: 'about.title'
  },
  canActivate: [UserRouteAccessService],
  children: [TERMS_ROUTE, PRIVACY_ROUTE, COOKIES_ROUTE, IMPRINT_ROUTE]
};
