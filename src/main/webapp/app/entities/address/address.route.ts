import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Address, IAddress } from 'app/shared/model/address.model';
import { AddressService } from './address.service';
import { AddressComponent } from './address.component';
import { AddressDetailComponent } from './address-detail.component';
import { AddressUpdateComponent } from './address-update.component';
import { AddressDeletePopupComponent } from './address-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class AddressResolve implements Resolve<IAddress> {
  constructor(private service: AddressService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAddress> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((address: HttpResponse<Address>) => address.body));
    }
    return of(new Address());
  }
}

export const addressRoute: Routes = [
  {
    path: '',
    component: AddressComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.address.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AddressDetailComponent,
    resolve: {
      address: AddressResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.address.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AddressUpdateComponent,
    resolve: {
      address: AddressResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.address.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AddressUpdateComponent,
    resolve: {
      address: AddressResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.address.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const addressPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: AddressDeletePopupComponent,
    resolve: {
      address: AddressResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.address.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
