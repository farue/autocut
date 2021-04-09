import { Route } from '@angular/router';

import { ApplicationComponent } from './application.component';

export const APPLICATION_ROUTE: Route = {
  path: '',
  component: ApplicationComponent,
  data: {
    pageTitle: 'application.title',
  },
};
