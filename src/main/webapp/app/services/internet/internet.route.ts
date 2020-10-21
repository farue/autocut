import { Route } from '@angular/router';
import { InternetComponent } from './internet.component';

export const INTERNET_ROUTE: Route = {
  path: '',
  component: InternetComponent,
  data: {
    pageTitle: 'internet.title',
  },
};
