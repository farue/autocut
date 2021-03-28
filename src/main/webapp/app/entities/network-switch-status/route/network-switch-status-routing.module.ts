import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NetworkSwitchStatusComponent } from '../list/network-switch-status.component';
import { NetworkSwitchStatusDetailComponent } from '../detail/network-switch-status-detail.component';
import { NetworkSwitchStatusUpdateComponent } from '../update/network-switch-status-update.component';
import { NetworkSwitchStatusRoutingResolveService } from './network-switch-status-routing-resolve.service';

const networkSwitchStatusRoute: Routes = [
  {
    path: '',
    component: NetworkSwitchStatusComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NetworkSwitchStatusDetailComponent,
    resolve: {
      networkSwitchStatus: NetworkSwitchStatusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NetworkSwitchStatusUpdateComponent,
    resolve: {
      networkSwitchStatus: NetworkSwitchStatusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NetworkSwitchStatusUpdateComponent,
    resolve: {
      networkSwitchStatus: NetworkSwitchStatusRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(networkSwitchStatusRoute)],
  exports: [RouterModule],
})
export class NetworkSwitchStatusRoutingModule {}
