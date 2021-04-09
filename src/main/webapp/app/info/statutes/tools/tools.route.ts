import { Route } from '@angular/router';

import { ToolsComponent } from 'app/info/statutes/tools/tools.component';

export const TOOLS_ROUTE: Route = {
  path: 'tools',
  component: ToolsComponent,
  data: {
    authorities: [],
    pageTitle: 'statutes.tools.title',
  },
};
