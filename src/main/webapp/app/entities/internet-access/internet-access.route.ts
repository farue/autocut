import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IInternetAccess, InternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';
import { InternetAccessComponent } from './internet-access.component';
import { InternetAccessDetailComponent } from './internet-access-detail.component';
import { InternetAccessUpdateComponent } from './internet-access-update.component';

@Injectable({ providedIn: 'root' })
export class InternetAccessResolve implements Resolve<IInternetAccess> {
  constructor(private service: InternetAccessService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInternetAccess> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((internetAccess: HttpResponse<InternetAccess>) => {
          if (internetAccess.body) {
            return of(internetAccess.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
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
