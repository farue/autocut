import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ITenant, Tenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';
import { TenantComponent } from './tenant.component';
import { TenantDetailComponent } from './tenant-detail.component';
import { TenantUpdateComponent } from './tenant-update.component';

@Injectable({ providedIn: 'root' })
export class TenantResolve implements Resolve<ITenant> {
  constructor(private service: TenantService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITenant> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((tenant: HttpResponse<Tenant>) => {
          if (tenant.body) {
            return of(tenant.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Tenant());
  }
}

export const tenantRoute: Routes = [
  {
    path: '',
    component: TenantComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenant.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TenantDetailComponent,
    resolve: {
      tenant: TenantResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenant.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TenantUpdateComponent,
    resolve: {
      tenant: TenantResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenant.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TenantUpdateComponent,
    resolve: {
      tenant: TenantResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenant.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
