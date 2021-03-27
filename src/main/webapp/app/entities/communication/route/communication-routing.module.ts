import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CommunicationComponent } from '../list/communication.component';
import { CommunicationDetailComponent } from '../detail/communication-detail.component';
import { CommunicationUpdateComponent } from '../update/communication-update.component';
import { CommunicationRoutingResolveService } from './communication-routing-resolve.service';

const communicationRoute: Routes = [
  {
    path: '',
    component: CommunicationComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CommunicationDetailComponent,
    resolve: {
      communication: CommunicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CommunicationUpdateComponent,
    resolve: {
      communication: CommunicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CommunicationUpdateComponent,
    resolve: {
      communication: CommunicationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(communicationRoute)],
  exports: [RouterModule],
})
export class CommunicationRoutingModule {}
