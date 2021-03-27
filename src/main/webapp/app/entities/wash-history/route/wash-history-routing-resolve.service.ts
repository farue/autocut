import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWashHistory, WashHistory } from '../wash-history.model';
import { WashHistoryService } from '../service/wash-history.service';

@Injectable({ providedIn: 'root' })
export class WashHistoryRoutingResolveService implements Resolve<IWashHistory> {
  constructor(protected service: WashHistoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWashHistory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((washHistory: HttpResponse<WashHistory>) => {
          if (washHistory.body) {
            return of(washHistory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new WashHistory());
  }
}
