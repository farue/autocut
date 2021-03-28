import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { InternalTransactionComponent } from '../list/internal-transaction.component';
import { InternalTransactionDetailComponent } from '../detail/internal-transaction-detail.component';
import { InternalTransactionUpdateComponent } from '../update/internal-transaction-update.component';
import { InternalTransactionRoutingResolveService } from './internal-transaction-routing-resolve.service';

const internalTransactionRoute: Routes = [
  {
    path: '',
    component: InternalTransactionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InternalTransactionDetailComponent,
    resolve: {
      internalTransaction: InternalTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InternalTransactionUpdateComponent,
    resolve: {
      internalTransaction: InternalTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InternalTransactionUpdateComponent,
    resolve: {
      internalTransaction: InternalTransactionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(internalTransactionRoute)],
  exports: [RouterModule],
})
export class InternalTransactionRoutingModule {}
