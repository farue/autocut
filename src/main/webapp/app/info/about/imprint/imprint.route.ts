import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ImprintComponent } from './imprint.component';

export const IMPRINT_ROUTE: Route = {
  path: 'impressum',
  component: ImprintComponent,
  data: {
    authorities: [],
    pageTitle: 'about.impressum.title',
  },
  canActivate: [UserRouteAccessService],
};
