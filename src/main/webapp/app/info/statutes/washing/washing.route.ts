import { Route } from '@angular/router';

import { WashingComponent } from './washing.component';

export const WASHING_ROUTE: Route = {
  path: 'washing',
  component: WashingComponent,
  data: {
    authorities: [],
    pageTitle: 'washing.title',
  },
};
