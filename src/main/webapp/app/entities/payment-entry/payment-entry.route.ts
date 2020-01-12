import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IPaymentEntry, PaymentEntry } from 'app/shared/model/payment-entry.model';
import { PaymentEntryService } from './payment-entry.service';
import { PaymentEntryComponent } from './payment-entry.component';
import { PaymentEntryDetailComponent } from './payment-entry-detail.component';
import { PaymentEntryUpdateComponent } from './payment-entry-update.component';

@Injectable({ providedIn: 'root' })
export class PaymentEntryResolve implements Resolve<IPaymentEntry> {
  constructor(private service: PaymentEntryService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaymentEntry> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((paymentEntry: HttpResponse<PaymentEntry>) => {
          if (paymentEntry.body) {
            return of(paymentEntry.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PaymentEntry());
  }
}

export const paymentEntryRoute: Routes = [
  {
    path: '',
    component: PaymentEntryComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentEntry.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PaymentEntryDetailComponent,
    resolve: {
      paymentEntry: PaymentEntryResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentEntry.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PaymentEntryUpdateComponent,
    resolve: {
      paymentEntry: PaymentEntryResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentEntry.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PaymentEntryUpdateComponent,
    resolve: {
      paymentEntry: PaymentEntryResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.paymentEntry.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
