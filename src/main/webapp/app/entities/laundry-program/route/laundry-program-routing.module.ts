import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LaundryProgramComponent } from '../list/laundry-program.component';
import { LaundryProgramDetailComponent } from '../detail/laundry-program-detail.component';
import { LaundryProgramUpdateComponent } from '../update/laundry-program-update.component';
import { LaundryProgramRoutingResolveService } from './laundry-program-routing-resolve.service';

const laundryProgramRoute: Routes = [
  {
    path: '',
    component: LaundryProgramComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LaundryProgramDetailComponent,
    resolve: {
      laundryProgram: LaundryProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LaundryProgramUpdateComponent,
    resolve: {
      laundryProgram: LaundryProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LaundryProgramUpdateComponent,
    resolve: {
      laundryProgram: LaundryProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(laundryProgramRoute)],
  exports: [RouterModule],
})
export class LaundryProgramRoutingModule {}
