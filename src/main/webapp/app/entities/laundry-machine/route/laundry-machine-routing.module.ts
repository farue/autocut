import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LaundryMachineComponent } from '../list/laundry-machine.component';
import { LaundryMachineDetailComponent } from '../detail/laundry-machine-detail.component';
import { LaundryMachineUpdateComponent } from '../update/laundry-machine-update.component';
import { LaundryMachineRoutingResolveService } from './laundry-machine-routing-resolve.service';

const laundryMachineRoute: Routes = [
  {
    path: '',
    component: LaundryMachineComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LaundryMachineDetailComponent,
    resolve: {
      laundryMachine: LaundryMachineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LaundryMachineUpdateComponent,
    resolve: {
      laundryMachine: LaundryMachineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LaundryMachineUpdateComponent,
    resolve: {
      laundryMachine: LaundryMachineRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(laundryMachineRoute)],
  exports: [RouterModule],
})
export class LaundryMachineRoutingModule {}
