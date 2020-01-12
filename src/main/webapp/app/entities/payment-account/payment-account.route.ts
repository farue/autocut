import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IPaymentAccount, PaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';
import { PaymentAccountComponent } from './payment-account.component';
import { PaymentAccountDetailComponent } from './payment-account-detail.component';
import { PaymentAccountUpdateComponent } from './payment-account-update.component';

@Injectable({ providedIn: 'root' })
export class PaymentAccountResolve implements Resolve<IPaymentAccount> {
  constructor(private service: PaymentAccountService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaymentAccount> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((paymentAccount: HttpResponse<PaymentAccount>) => {
          if (paymentAccount.body) {
            return of(paymentAccount.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PaymentAccount());
  }
}

export const paymentAccountRoute: Routes = [
  {
    path: '',
    component: PaymentAccountComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentAccount.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PaymentAccountDetailComponent,
    resolve: {
      paymentAccount: PaymentAccountResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentAccount.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PaymentAccountUpdateComponent,
    resolve: {
      paymentAccount: PaymentAccountResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentAccount.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PaymentAccountUpdateComponent,
    resolve: {
      paymentAccount: PaymentAccountResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentAccount.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
