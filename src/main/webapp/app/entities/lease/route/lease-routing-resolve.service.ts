import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILease, Lease } from '../lease.model';
import { LeaseService } from '../service/lease.service';

@Injectable({ providedIn: 'root' })
export class LeaseRoutingResolveService implements Resolve<ILease> {
  constructor(protected service: LeaseService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILease> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((lease: HttpResponse<Lease>) => {
          if (lease.body) {
            return of(lease.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Lease());
  }
}
