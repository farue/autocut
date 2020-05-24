import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ISecurityPolicy, SecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';
import { SecurityPolicyComponent } from './security-policy.component';
import { SecurityPolicyDetailComponent } from './security-policy-detail.component';
import { SecurityPolicyUpdateComponent } from './security-policy-update.component';

@Injectable({ providedIn: 'root' })
export class SecurityPolicyResolve implements Resolve<ISecurityPolicy> {
  constructor(private service: SecurityPolicyService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISecurityPolicy> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((securityPolicy: HttpResponse<SecurityPolicy>) => {
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

export const securityPolicyRoute: Routes = [
  {
    path: '',
    component: SecurityPolicyComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.securityPolicy.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SecurityPolicyDetailComponent,
    resolve: {
      securityPolicy: SecurityPolicyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.securityPolicy.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SecurityPolicyUpdateComponent,
    resolve: {
      securityPolicy: SecurityPolicyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.securityPolicy.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SecurityPolicyUpdateComponent,
    resolve: {
      securityPolicy: SecurityPolicyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.securityPolicy.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
