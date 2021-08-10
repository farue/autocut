import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BroadcastMessageTextComponent } from '../list/broadcast-message-text.component';
import { BroadcastMessageTextDetailComponent } from '../detail/broadcast-message-text-detail.component';
import { BroadcastMessageTextUpdateComponent } from '../update/broadcast-message-text-update.component';
import { BroadcastMessageTextRoutingResolveService } from './broadcast-message-text-routing-resolve.service';

const broadcastMessageTextRoute: Routes = [
  {
    path: '',
    component: BroadcastMessageTextComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BroadcastMessageTextDetailComponent,
    resolve: {
      broadcastMessageText: BroadcastMessageTextRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BroadcastMessageTextUpdateComponent,
    resolve: {
      broadcastMessageText: BroadcastMessageTextRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BroadcastMessageTextUpdateComponent,
    resolve: {
      broadcastMessageText: BroadcastMessageTextRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(broadcastMessageTextRoute)],
  exports: [RouterModule],
})
export class BroadcastMessageTextRoutingModule {}
