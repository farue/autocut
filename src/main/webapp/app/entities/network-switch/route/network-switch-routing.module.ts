import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NetworkSwitchComponent } from '../list/network-switch.component';
import { NetworkSwitchDetailComponent } from '../detail/network-switch-detail.component';
import { NetworkSwitchUpdateComponent } from '../update/network-switch-update.component';
import { NetworkSwitchRoutingResolveService } from './network-switch-routing-resolve.service';

const networkSwitchRoute: Routes = [
  {
    path: '',
    component: NetworkSwitchComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NetworkSwitchDetailComponent,
    resolve: {
      networkSwitch: NetworkSwitchRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NetworkSwitchUpdateComponent,
    resolve: {
      networkSwitch: NetworkSwitchRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NetworkSwitchUpdateComponent,
    resolve: {
      networkSwitch: NetworkSwitchRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(networkSwitchRoute)],
  exports: [RouterModule],
})
export class NetworkSwitchRoutingModule {}
