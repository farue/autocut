import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Apartment, IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from './apartment.service';
import { ApartmentComponent } from './apartment.component';
import { ApartmentDetailComponent } from './apartment-detail.component';
import { ApartmentUpdateComponent } from './apartment-update.component';
import { ApartmentDeletePopupComponent } from './apartment-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class ApartmentResolve implements Resolve<IApartment> {
  constructor(private service: ApartmentService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IApartment> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((apartment: HttpResponse<Apartment>) => apartment.body));
    }
    return of(new Apartment());
  }
}

export const apartmentRoute: Routes = [
  {
    path: '',
    component: ApartmentComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.apartment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ApartmentDetailComponent,
    resolve: {
      apartment: ApartmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.apartment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ApartmentUpdateComponent,
    resolve: {
      apartment: ApartmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.apartment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ApartmentUpdateComponent,
    resolve: {
      apartment: ApartmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.apartment.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const apartmentPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ApartmentDeletePopupComponent,
    resolve: {
      apartment: ApartmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.apartment.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
