import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { BankTransaction, IBankTransaction } from 'app/shared/model/bank-transaction.model';
import { BankTransactionService } from './bank-transaction.service';
import { BankTransactionComponent } from './bank-transaction.component';
import { BankTransactionDetailComponent } from './bank-transaction-detail.component';
import { BankTransactionUpdateComponent } from './bank-transaction-update.component';

@Injectable({ providedIn: 'root' })
export class BankTransactionResolve implements Resolve<IBankTransaction> {
  constructor(private service: BankTransactionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBankTransaction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((bankTransaction: HttpResponse<BankTransaction>) => {
          if (bankTransaction.body) {
            return of(bankTransaction.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BankTransaction());
  }
}

export const bankTransactionRoute: Routes = [
  {
    path: '',
    component: BankTransactionComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.bankTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BankTransactionDetailComponent,
    resolve: {
      bankTransaction: BankTransactionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.bankTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BankTransactionUpdateComponent,
    resolve: {
      bankTransaction: BankTransactionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.bankTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BankTransactionUpdateComponent,
    resolve: {
      bankTransaction: BankTransactionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.bankTransaction.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
