import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInternalTransaction, InternalTransaction } from '../internal-transaction.model';
import { InternalTransactionService } from '../service/internal-transaction.service';

@Injectable({ providedIn: 'root' })
export class InternalTransactionRoutingResolveService implements Resolve<IInternalTransaction> {
  constructor(protected service: InternalTransactionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInternalTransaction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((internalTransaction: HttpResponse<InternalTransaction>) => {
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
