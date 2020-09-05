import { Route } from '@angular/router';

import { TransactionComponent } from './transaction.component';

export const TRANSACTION_ROUTE: Route = {
  path: '',
  component: TransactionComponent,
  data: {
    pageTitle: 'transaction.title',
  },
};
