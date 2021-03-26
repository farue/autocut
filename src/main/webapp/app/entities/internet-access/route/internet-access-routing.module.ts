import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { InternetAccessComponent } from '../list/internet-access.component';
import { InternetAccessDetailComponent } from '../detail/internet-access-detail.component';
import { InternetAccessUpdateComponent } from '../update/internet-access-update.component';
import { InternetAccessRoutingResolveService } from './internet-access-routing-resolve.service';

const internetAccessRoute: Routes = [
  {
    path: '',
    component: InternetAccessComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InternetAccessDetailComponent,
    resolve: {
      internetAccess: InternetAccessRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InternetAccessUpdateComponent,
    resolve: {
      internetAccess: InternetAccessRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InternetAccessUpdateComponent,
    resolve: {
      internetAccess: InternetAccessRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(internetAccessRoute)],
  exports: [RouterModule],
})
export class InternetAccessRoutingModule {}
