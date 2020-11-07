import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IInternalTransaction, InternalTransaction } from 'app/shared/model/internal-transaction.model';
import { InternalTransactionService } from './internal-transaction.service';
import { InternalTransactionComponent } from './internal-transaction.component';
import { InternalTransactionDetailComponent } from './internal-transaction-detail.component';
import { InternalTransactionUpdateComponent } from './internal-transaction-update.component';

@Injectable({ providedIn: 'root' })
export class InternalTransactionResolve implements Resolve<IInternalTransaction> {
  constructor(private service: InternalTransactionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInternalTransaction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((internalTransaction: HttpResponse<InternalTransaction>) => {
          if (internalTransaction.body) {
            return of(internalTransaction.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new InternalTransaction());
  }
}

export const internalTransactionRoute: Routes = [
  {
    path: '',
    component: InternalTransactionComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.internalTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InternalTransactionDetailComponent,
    resolve: {
      internalTransaction: InternalTransactionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.internalTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InternalTransactionUpdateComponent,
    resolve: {
      internalTransaction: InternalTransactionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.internalTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InternalTransactionUpdateComponent,
    resolve: {
      internalTransaction: InternalTransactionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.internalTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
