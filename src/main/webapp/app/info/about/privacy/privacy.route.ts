import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PrivacyComponent } from './privacy.component';

export const PRIVACY_ROUTE: Route = {
  path: 'privacy',
  component: PrivacyComponent,
  data: {
    authorities: [],
    pageTitle: 'about.privacy.title',
  },
  canActivate: [UserRouteAccessService],
};
