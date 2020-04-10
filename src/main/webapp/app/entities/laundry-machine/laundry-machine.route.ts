import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ILaundryMachine, LaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineService } from './laundry-machine.service';
import { LaundryMachineComponent } from './laundry-machine.component';
import { LaundryMachineDetailComponent } from './laundry-machine-detail.component';
import { LaundryMachineUpdateComponent } from './laundry-machine-update.component';

@Injectable({ providedIn: 'root' })
export class LaundryMachineResolve implements Resolve<ILaundryMachine> {
  constructor(private service: LaundryMachineService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILaundryMachine> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((laundryMachine: HttpResponse<LaundryMachine>) => {
          if (laundryMachine.body) {
            return of(laundryMachine.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LaundryMachine());
  }
}

export const laundryMachineRoute: Routes = [
  {
    path: '',
    component: LaundryMachineComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: LaundryMachineDetailComponent,
    resolve: {
      laundryMachine: LaundryMachineResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: LaundryMachineUpdateComponent,
    resolve: {
      laundryMachine: LaundryMachineResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: LaundryMachineUpdateComponent,
    resolve: {
      laundryMachine: LaundryMachineResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.laundryMachine.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
