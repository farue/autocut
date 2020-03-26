import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ITotp, Totp } from 'app/shared/model/totp.model';
import { TotpService } from './totp.service';
import { TotpComponent } from './totp.component';
import { TotpDetailComponent } from './totp-detail.component';
import { TotpUpdateComponent } from './totp-update.component';

@Injectable({ providedIn: 'root' })
export class TotpResolve implements Resolve<ITotp> {
  constructor(private service: TotpService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITotp> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((totp: HttpResponse<Totp>) => {
          if (totp.body) {
            return of(totp.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Totp());
  }
}

export const totpRoute: Routes = [
  {
    path: '',
    component: TotpComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.totp.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: TotpDetailComponent,
    resolve: {
      totp: TotpResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.totp.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: TotpUpdateComponent,
    resolve: {
      totp: TotpResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.totp.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: TotpUpdateComponent,
    resolve: {
      totp: TotpResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.totp.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
