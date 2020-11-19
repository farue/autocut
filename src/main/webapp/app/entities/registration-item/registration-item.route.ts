import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IRegistrationItem, RegistrationItem } from 'app/shared/model/registration-item.model';
import { RegistrationItemService } from './registration-item.service';
import { RegistrationItemComponent } from './registration-item.component';
import { RegistrationItemDetailComponent } from './registration-item-detail.component';
import { RegistrationItemUpdateComponent } from './registration-item-update.component';

@Injectable({ providedIn: 'root' })
export class RegistrationItemResolve implements Resolve<IRegistrationItem> {
  constructor(private service: RegistrationItemService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRegistrationItem> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((registrationItem: HttpResponse<RegistrationItem>) => {
          if (registrationItem.body) {
            return of(registrationItem.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new RegistrationItem());
  }
}

export const registrationItemRoute: Routes = [
  {
    path: '',
    component: RegistrationItemComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.registrationItem.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RegistrationItemDetailComponent,
    resolve: {
      registrationItem: RegistrationItemResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.registrationItem.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RegistrationItemUpdateComponent,
    resolve: {
      registrationItem: RegistrationItemResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.registrationItem.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RegistrationItemUpdateComponent,
    resolve: {
      registrationItem: RegistrationItemResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.registrationItem.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
