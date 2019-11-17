import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ITenant, Tenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';
import { TenantComponent } from './tenant.component';
import { TenantDetailComponent } from './tenant-detail.component';
import { TenantUpdateComponent } from './tenant-update.component';
import { TenantDeletePopupComponent } from './tenant-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class TenantResolve implements Resolve<ITenant> {
  constructor(private service: TenantService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITenant> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((tenant: HttpResponse<Tenant>) => tenant.body));
    }
    return of(new Tenant());
  }
}

export const tenantRoute: Routes = [
  {
    path: '',
    component: TenantComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenant.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: TenantDetailComponent,
    resolve: {
      tenant: TenantResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenant.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: TenantUpdateComponent,
    resolve: {
      tenant: TenantResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenant.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: TenantUpdateComponent,
    resolve: {
      tenant: TenantResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenant.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const tenantPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: TenantDeletePopupComponent,
    resolve: {
      tenant: TenantResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenant.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
