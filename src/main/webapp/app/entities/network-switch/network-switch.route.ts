import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { INetworkSwitch, NetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';
import { NetworkSwitchComponent } from './network-switch.component';
import { NetworkSwitchDetailComponent } from './network-switch-detail.component';
import { NetworkSwitchUpdateComponent } from './network-switch-update.component';

@Injectable({ providedIn: 'root' })
export class NetworkSwitchResolve implements Resolve<INetworkSwitch> {
  constructor(private service: NetworkSwitchService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INetworkSwitch> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((networkSwitch: HttpResponse<NetworkSwitch>) => {
          if (networkSwitch.body) {
            return of(networkSwitch.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NetworkSwitch());
  }
}

export const networkSwitchRoute: Routes = [
  {
    path: '',
    component: NetworkSwitchComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.networkSwitch.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: NetworkSwitchDetailComponent,
    resolve: {
      networkSwitch: NetworkSwitchResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.networkSwitch.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: NetworkSwitchUpdateComponent,
    resolve: {
      networkSwitch: NetworkSwitchResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.networkSwitch.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: NetworkSwitchUpdateComponent,
    resolve: {
      networkSwitch: NetworkSwitchResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.networkSwitch.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
