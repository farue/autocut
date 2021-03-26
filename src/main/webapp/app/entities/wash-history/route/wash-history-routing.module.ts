import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WashHistoryComponent } from '../list/wash-history.component';
import { WashHistoryDetailComponent } from '../detail/wash-history-detail.component';
import { WashHistoryUpdateComponent } from '../update/wash-history-update.component';
import { WashHistoryRoutingResolveService } from './wash-history-routing-resolve.service';

const washHistoryRoute: Routes = [
  {
    path: '',
    component: WashHistoryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WashHistoryDetailComponent,
    resolve: {
      washHistory: WashHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: WashHistoryUpdateComponent,
    resolve: {
      washHistory: WashHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WashHistoryUpdateComponent,
    resolve: {
      washHistory: WashHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(washHistoryRoute)],
  exports: [RouterModule],
})
export class WashHistoryRoutingModule {}
