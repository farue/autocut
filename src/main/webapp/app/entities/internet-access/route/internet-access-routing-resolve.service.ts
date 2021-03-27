import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInternetAccess, InternetAccess } from '../internet-access.model';
import { InternetAccessService } from '../service/internet-access.service';

@Injectable({ providedIn: 'root' })
export class InternetAccessRoutingResolveService implements Resolve<IInternetAccess> {
  constructor(protected service: InternetAccessService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInternetAccess> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((internetAccess: HttpResponse<InternetAccess>) => {
          if (internetAccess.body) {
            return of(internetAccess.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new InternetAccess());
  }
}
