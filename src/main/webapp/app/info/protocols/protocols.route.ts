import { Route } from '@angular/router';
import { ProtocolsComponent } from 'app/info/protocols/protocols.component';

export const PROTOCOLS_ROUTE: Route = {
  path: '',
  component: ProtocolsComponent,
  data: {
    pageTitle: 'protocols.title',
  },
};
