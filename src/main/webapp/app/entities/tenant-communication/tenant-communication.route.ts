import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ITenantCommunication, TenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';
import { TenantCommunicationComponent } from './tenant-communication.component';
import { TenantCommunicationDetailComponent } from './tenant-communication-detail.component';
import { TenantCommunicationUpdateComponent } from './tenant-communication-update.component';
import { TenantCommunicationDeletePopupComponent } from './tenant-communication-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class TenantCommunicationResolve implements Resolve<ITenantCommunication> {
  constructor(private service: TenantCommunicationService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITenantCommunication> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((tenantCommunication: HttpResponse<TenantCommunication>) => tenantCommunication.body));
    }
    return of(new TenantCommunication());
  }
}

export const tenantCommunicationRoute: Routes = [
  {
    path: '',
    component: TenantCommunicationComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenantCommunication.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: TenantCommunicationDetailComponent,
    resolve: {
      tenantCommunication: TenantCommunicationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenantCommunication.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: TenantCommunicationUpdateComponent,
    resolve: {
      tenantCommunication: TenantCommunicationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenantCommunication.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: TenantCommunicationUpdateComponent,
    resolve: {
      tenantCommunication: TenantCommunicationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenantCommunication.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const tenantCommunicationPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: TenantCommunicationDeletePopupComponent,
    resolve: {
      tenantCommunication: TenantCommunicationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.tenantCommunication.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
