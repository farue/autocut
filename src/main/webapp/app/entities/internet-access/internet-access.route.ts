import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IInternetAccess, InternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';
import { InternetAccessComponent } from './internet-access.component';
import { InternetAccessDetailComponent } from './internet-access-detail.component';
import { InternetAccessUpdateComponent } from './internet-access-update.component';
import { InternetAccessDeletePopupComponent } from './internet-access-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class InternetAccessResolve implements Resolve<IInternetAccess> {
  constructor(private service: InternetAccessService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInternetAccess> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((internetAccess: HttpResponse<InternetAccess>) => internetAccess.body));
    }
    return of(new InternetAccess());
  }
}

export const internetAccessRoute: Routes = [
  {
    path: '',
    component: InternetAccessComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.internetAccess.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: InternetAccessDetailComponent,
    resolve: {
      internetAccess: InternetAccessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.internetAccess.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: InternetAccessUpdateComponent,
    resolve: {
      internetAccess: InternetAccessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.internetAccess.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: InternetAccessUpdateComponent,
    resolve: {
      internetAccess: InternetAccessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.internetAccess.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const internetAccessPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: InternetAccessDeletePopupComponent,
    resolve: {
      internetAccess: InternetAccessResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.internetAccess.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
