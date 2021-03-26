import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SecurityPolicyComponent } from '../list/security-policy.component';
import { SecurityPolicyDetailComponent } from '../detail/security-policy-detail.component';
import { SecurityPolicyUpdateComponent } from '../update/security-policy-update.component';
import { SecurityPolicyRoutingResolveService } from './security-policy-routing-resolve.service';

const securityPolicyRoute: Routes = [
  {
    path: '',
    component: SecurityPolicyComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SecurityPolicyDetailComponent,
    resolve: {
      securityPolicy: SecurityPolicyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SecurityPolicyUpdateComponent,
    resolve: {
      securityPolicy: SecurityPolicyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SecurityPolicyUpdateComponent,
    resolve: {
      securityPolicy: SecurityPolicyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(securityPolicyRoute)],
  exports: [RouterModule],
})
export class SecurityPolicyRoutingModule {}
