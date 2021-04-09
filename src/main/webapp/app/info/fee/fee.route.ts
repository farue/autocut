import { Route } from '@angular/router';

import { FeeComponent } from './fee.component';

export const FEE_ROUTE: Route = {
  path: '',
  component: FeeComponent,
  data: {
    pageTitle: 'fee.title',
  },
};
