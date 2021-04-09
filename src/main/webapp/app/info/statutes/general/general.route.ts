import { Route } from '@angular/router';

import { GeneralComponent } from './general.component';

export const GENERAL_ROUTE: Route = {
  path: 'general',
  component: GeneralComponent,
  data: {
    authorities: [],
    pageTitle: 'statutes.general.title',
  },
};
