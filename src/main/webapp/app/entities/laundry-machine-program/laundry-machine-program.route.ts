import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ILaundryMachineProgram, LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { LaundryMachineProgramService } from './laundry-machine-program.service';
import { LaundryMachineProgramComponent } from './laundry-machine-program.component';
import { LaundryMachineProgramDetailComponent } from './laundry-machine-program-detail.component';
import { LaundryMachineProgramUpdateComponent } from './laundry-machine-program-update.component';

@Injectable({ providedIn: 'root' })
export class LaundryMachineProgramResolve implements Resolve<ILaundryMachineProgram> {
  constructor(private service: LaundryMachineProgramService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILaundryMachineProgram> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((laundryMachineProgram: HttpResponse<LaundryMachineProgram>) => {
          if (laundryMachineProgram.body) {
            return of(laundryMachineProgram.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LaundryMachineProgram());
  }
}

export const laundryMachineProgramRoute: Routes = [
  {
    path: '',
    component: LaundryMachineProgramComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachineProgram.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LaundryMachineProgramDetailComponent,
    resolve: {
      laundryMachineProgram: LaundryMachineProgramResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachineProgram.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LaundryMachineProgramUpdateComponent,
    resolve: {
      laundryMachineProgram: LaundryMachineProgramResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachineProgram.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LaundryMachineProgramUpdateComponent,
    resolve: {
      laundryMachineProgram: LaundryMachineProgramResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachineProgram.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
