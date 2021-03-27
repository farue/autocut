import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LeaseComponent } from '../list/lease.component';
import { LeaseDetailComponent } from '../detail/lease-detail.component';
import { LeaseUpdateComponent } from '../update/lease-update.component';
import { LeaseRoutingResolveService } from './lease-routing-resolve.service';

const leaseRoute: Routes = [
  {
    path: '',
    component: LeaseComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LeaseDetailComponent,
    resolve: {
      lease: LeaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LeaseUpdateComponent,
    resolve: {
      lease: LeaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LeaseUpdateComponent,
    resolve: {
      lease: LeaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(leaseRoute)],
  exports: [RouterModule],
})
export class LeaseRoutingModule {}
