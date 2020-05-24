import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ILease, Lease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';
import { LeaseComponent } from './lease.component';
import { LeaseDetailComponent } from './lease-detail.component';
import { LeaseUpdateComponent } from './lease-update.component';

@Injectable({ providedIn: 'root' })
export class LeaseResolve implements Resolve<ILease> {
  constructor(private service: LeaseService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILease> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((lease: HttpResponse<Lease>) => {
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

export const leaseRoute: Routes = [
  {
    path: '',
    component: LeaseComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.lease.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LeaseDetailComponent,
    resolve: {
      lease: LeaseResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.lease.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LeaseUpdateComponent,
    resolve: {
      lease: LeaseResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.lease.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LeaseUpdateComponent,
    resolve: {
      lease: LeaseResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.lease.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
