import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ApartmentComponent } from '../list/apartment.component';
import { ApartmentDetailComponent } from '../detail/apartment-detail.component';
import { ApartmentUpdateComponent } from '../update/apartment-update.component';
import { ApartmentRoutingResolveService } from './apartment-routing-resolve.service';

const apartmentRoute: Routes = [
  {
    path: '',
    component: ApartmentComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ApartmentDetailComponent,
    resolve: {
      apartment: ApartmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ApartmentUpdateComponent,
    resolve: {
      apartment: ApartmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ApartmentUpdateComponent,
    resolve: {
      apartment: ApartmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(apartmentRoute)],
  exports: [RouterModule],
})
export class ApartmentRoutingModule {}
