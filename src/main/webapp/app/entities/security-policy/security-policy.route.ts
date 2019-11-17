import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISecurityPolicy, SecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';
import { SecurityPolicyComponent } from './security-policy.component';
import { SecurityPolicyDetailComponent } from './security-policy-detail.component';
import { SecurityPolicyUpdateComponent } from './security-policy-update.component';
import { SecurityPolicyDeletePopupComponent } from './security-policy-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class SecurityPolicyResolve implements Resolve<ISecurityPolicy> {
  constructor(private service: SecurityPolicyService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISecurityPolicy> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((securityPolicy: HttpResponse<SecurityPolicy>) => securityPolicy.body));
    }
    return of(new SecurityPolicy());
  }
}

export const securityPolicyRoute: Routes = [
  {
    path: '',
    component: SecurityPolicyComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.securityPolicy.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SecurityPolicyDetailComponent,
    resolve: {
      securityPolicy: SecurityPolicyResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.securityPolicy.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: SecurityPolicyUpdateComponent,
    resolve: {
      securityPolicy: SecurityPolicyResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.securityPolicy.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: SecurityPolicyUpdateComponent,
    resolve: {
      securityPolicy: SecurityPolicyResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.securityPolicy.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const securityPolicyPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: SecurityPolicyDeletePopupComponent,
    resolve: {
      securityPolicy: SecurityPolicyResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.securityPolicy.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
