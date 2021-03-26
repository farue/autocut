import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LaundryMachineProgramComponent } from '../list/laundry-machine-program.component';
import { LaundryMachineProgramDetailComponent } from '../detail/laundry-machine-program-detail.component';
import { LaundryMachineProgramUpdateComponent } from '../update/laundry-machine-program-update.component';
import { LaundryMachineProgramRoutingResolveService } from './laundry-machine-program-routing-resolve.service';

const laundryMachineProgramRoute: Routes = [
  {
    path: '',
    component: LaundryMachineProgramComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LaundryMachineProgramDetailComponent,
    resolve: {
      laundryMachineProgram: LaundryMachineProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LaundryMachineProgramUpdateComponent,
    resolve: {
      laundryMachineProgram: LaundryMachineProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LaundryMachineProgramUpdateComponent,
    resolve: {
      laundryMachineProgram: LaundryMachineProgramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(laundryMachineProgramRoute)],
  exports: [RouterModule],
})
export class LaundryMachineProgramRoutingModule {}
