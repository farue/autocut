import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IPaymentAccount, PaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';
import { PaymentAccountComponent } from './payment-account.component';
import { PaymentAccountDetailComponent } from './payment-account-detail.component';
import { PaymentAccountUpdateComponent } from './payment-account-update.component';
import { PaymentAccountDeletePopupComponent } from './payment-account-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class PaymentAccountResolve implements Resolve<IPaymentAccount> {
  constructor(private service: PaymentAccountService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaymentAccount> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((paymentAccount: HttpResponse<PaymentAccount>) => paymentAccount.body));
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

export const paymentAccountPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: PaymentAccountDeletePopupComponent,
    resolve: {
      paymentAccount: PaymentAccountResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentAccount.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
