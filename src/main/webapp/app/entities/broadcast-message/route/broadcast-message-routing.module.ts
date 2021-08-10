import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BroadcastMessageComponent } from '../list/broadcast-message.component';
import { BroadcastMessageDetailComponent } from '../detail/broadcast-message-detail.component';
import { BroadcastMessageUpdateComponent } from '../update/broadcast-message-update.component';
import { BroadcastMessageRoutingResolveService } from './broadcast-message-routing-resolve.service';

const broadcastMessageRoute: Routes = [
  {
    path: '',
    component: BroadcastMessageComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BroadcastMessageDetailComponent,
    resolve: {
      broadcastMessage: BroadcastMessageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BroadcastMessageUpdateComponent,
    resolve: {
      broadcastMessage: BroadcastMessageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BroadcastMessageUpdateComponent,
    resolve: {
      broadcastMessage: BroadcastMessageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(broadcastMessageRoute)],
  exports: [RouterModule],
})
export class BroadcastMessageRoutingModule {}
