import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITenantCommunication, TenantCommunication } from '../tenant-communication.model';
import { TenantCommunicationService } from '../service/tenant-communication.service';

@Injectable({ providedIn: 'root' })
export class TenantCommunicationRoutingResolveService implements Resolve<ITenantCommunication> {
  constructor(protected service: TenantCommunicationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITenantCommunication> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tenantCommunication: HttpResponse<TenantCommunication>) => {
          if (tenantCommunication.body) {
            return of(tenantCommunication.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TenantCommunication());
  }
}
