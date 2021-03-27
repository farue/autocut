import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TransactionBookComponent } from '../list/transaction-book.component';
import { TransactionBookDetailComponent } from '../detail/transaction-book-detail.component';
import { TransactionBookUpdateComponent } from '../update/transaction-book-update.component';
import { TransactionBookRoutingResolveService } from './transaction-book-routing-resolve.service';

const transactionBookRoute: Routes = [
  {
    path: '',
    component: TransactionBookComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TransactionBookDetailComponent,
    resolve: {
      transactionBook: TransactionBookRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TransactionBookUpdateComponent,
    resolve: {
      transactionBook: TransactionBookRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TransactionBookUpdateComponent,
    resolve: {
      transactionBook: TransactionBookRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(transactionBookRoute)],
  exports: [RouterModule],
})
export class TransactionBookRoutingModule {}
