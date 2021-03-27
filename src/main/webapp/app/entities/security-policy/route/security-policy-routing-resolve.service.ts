import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISecurityPolicy, SecurityPolicy } from '../security-policy.model';
import { SecurityPolicyService } from '../service/security-policy.service';

@Injectable({ providedIn: 'root' })
export class SecurityPolicyRoutingResolveService implements Resolve<ISecurityPolicy> {
  constructor(protected service: SecurityPolicyService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISecurityPolicy> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((securityPolicy: HttpResponse<SecurityPolicy>) => {
          if (securityPolicy.body) {
            return of(securityPolicy.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SecurityPolicy());
  }
}
