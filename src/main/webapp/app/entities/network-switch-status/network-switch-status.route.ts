import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { INetworkSwitchStatus, NetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';
import { NetworkSwitchStatusService } from './network-switch-status.service';
import { NetworkSwitchStatusComponent } from './network-switch-status.component';
import { NetworkSwitchStatusDetailComponent } from './network-switch-status-detail.component';
import { NetworkSwitchStatusUpdateComponent } from './network-switch-status-update.component';

@Injectable({ providedIn: 'root' })
export class NetworkSwitchStatusResolve implements Resolve<INetworkSwitchStatus> {
  constructor(private service: NetworkSwitchStatusService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INetworkSwitchStatus> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((networkSwitchStatus: HttpResponse<NetworkSwitchStatus>) => {
          if (networkSwitchStatus.body) {
            return of(networkSwitchStatus.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NetworkSwitchStatus());
  }
}

export const networkSwitchStatusRoute: Routes = [
  {
    path: '',
    component: NetworkSwitchStatusComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.networkSwitchStatus.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NetworkSwitchStatusDetailComponent,
    resolve: {
      networkSwitchStatus: NetworkSwitchStatusResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.networkSwitchStatus.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NetworkSwitchStatusUpdateComponent,
    resolve: {
      networkSwitchStatus: NetworkSwitchStatusResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.networkSwitchStatus.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NetworkSwitchStatusUpdateComponent,
    resolve: {
      networkSwitchStatus: NetworkSwitchStatusResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.networkSwitchStatus.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
