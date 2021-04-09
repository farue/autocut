import { Route } from '@angular/router';

import { WashingComponent } from 'app/services/washing/washing.component';

export const WASHING_ROUTE: Route = {
  path: '',
  component: WashingComponent,
  data: {
    pageTitle: 'washing.title',
  },
};
