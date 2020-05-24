import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ITenantCommunication, TenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';
import { TenantCommunicationComponent } from './tenant-communication.component';
import { TenantCommunicationDetailComponent } from './tenant-communication-detail.component';
import { TenantCommunicationUpdateComponent } from './tenant-communication-update.component';

@Injectable({ providedIn: 'root' })
export class TenantCommunicationResolve implements Resolve<ITenantCommunication> {
  constructor(private service: TenantCommunicationService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITenantCommunication> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((tenantCommunication: HttpResponse<TenantCommunication>) => {
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

export const tenantCommunicationRoute: Routes = [
  {
    path: '',
    component: TenantCommunicationComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenantCommunication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TenantCommunicationDetailComponent,
    resolve: {
      tenantCommunication: TenantCommunicationResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenantCommunication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TenantCommunicationUpdateComponent,
    resolve: {
      tenantCommunication: TenantCommunicationResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenantCommunication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TenantCommunicationUpdateComponent,
    resolve: {
      tenantCommunication: TenantCommunicationResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.tenantCommunication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
