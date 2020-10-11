import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ITransactionBook, TransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionBookService } from './transaction-book.service';
import { TransactionBookComponent } from './transaction-book.component';
import { TransactionBookDetailComponent } from './transaction-book-detail.component';
import { TransactionBookUpdateComponent } from './transaction-book-update.component';

@Injectable({ providedIn: 'root' })
export class TransactionBookResolve implements Resolve<ITransactionBook> {
  constructor(private service: TransactionBookService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITransactionBook> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((transactionBook: HttpResponse<TransactionBook>) => {
          if (transactionBook.body) {
            return of(transactionBook.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TransactionBook());
  }
}

export const transactionBookRoute: Routes = [
  {
    path: '',
    component: TransactionBookComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.transactionBook.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TransactionBookDetailComponent,
    resolve: {
      transactionBook: TransactionBookResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.transactionBook.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TransactionBookUpdateComponent,
    resolve: {
      transactionBook: TransactionBookResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.transactionBook.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TransactionBookUpdateComponent,
    resolve: {
      transactionBook: TransactionBookResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.transactionBook.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
