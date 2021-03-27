import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TenantCommunicationComponent } from '../list/tenant-communication.component';
import { TenantCommunicationDetailComponent } from '../detail/tenant-communication-detail.component';
import { TenantCommunicationUpdateComponent } from '../update/tenant-communication-update.component';
import { TenantCommunicationRoutingResolveService } from './tenant-communication-routing-resolve.service';

const tenantCommunicationRoute: Routes = [
  {
    path: '',
    component: TenantCommunicationComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TenantCommunicationDetailComponent,
    resolve: {
      tenantCommunication: TenantCommunicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TenantCommunicationUpdateComponent,
    resolve: {
      tenantCommunication: TenantCommunicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TenantCommunicationUpdateComponent,
    resolve: {
      tenantCommunication: TenantCommunicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tenantCommunicationRoute)],
  exports: [RouterModule],
})
export class TenantCommunicationRoutingModule {}
