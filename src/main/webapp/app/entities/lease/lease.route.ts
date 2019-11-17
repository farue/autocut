import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ILease, Lease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';
import { LeaseComponent } from './lease.component';
import { LeaseDetailComponent } from './lease-detail.component';
import { LeaseUpdateComponent } from './lease-update.component';
import { LeaseDeletePopupComponent } from './lease-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class LeaseResolve implements Resolve<ILease> {
  constructor(private service: LeaseService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILease> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((lease: HttpResponse<Lease>) => lease.body));
    }
    return of(new Lease());
  }
}

export const leaseRoute: Routes = [
  {
    path: '',
    component: LeaseComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.lease.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: LeaseDetailComponent,
    resolve: {
      lease: LeaseResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.lease.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: LeaseUpdateComponent,
    resolve: {
      lease: LeaseResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.lease.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: LeaseUpdateComponent,
    resolve: {
      lease: LeaseResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.lease.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const leasePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: LeaseDeletePopupComponent,
    resolve: {
      lease: LeaseResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.lease.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
