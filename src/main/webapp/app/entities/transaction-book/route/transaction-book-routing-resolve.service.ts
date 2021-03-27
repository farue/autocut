import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITransactionBook, TransactionBook } from '../transaction-book.model';
import { TransactionBookService } from '../service/transaction-book.service';

@Injectable({ providedIn: 'root' })
export class TransactionBookRoutingResolveService implements Resolve<ITransactionBook> {
  constructor(protected service: TransactionBookService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITransactionBook> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((transactionBook: HttpResponse<TransactionBook>) => {
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
