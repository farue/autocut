import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core';
import { SpokesmanComponent } from './spokesman.component';

export const SPOKESMAN_ROUTE: Route = {
  path: 'spokesman',
  component: SpokesmanComponent,
  data: {
    authorities: [],
    pageTitle: 'spokesman.title'
  },
  canActivate: [UserRouteAccessService]
};
