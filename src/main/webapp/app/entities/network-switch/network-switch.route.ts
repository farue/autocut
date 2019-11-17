import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { INetworkSwitch, NetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';
import { NetworkSwitchComponent } from './network-switch.component';
import { NetworkSwitchDetailComponent } from './network-switch-detail.component';
import { NetworkSwitchUpdateComponent } from './network-switch-update.component';
import { NetworkSwitchDeletePopupComponent } from './network-switch-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class NetworkSwitchResolve implements Resolve<INetworkSwitch> {
  constructor(private service: NetworkSwitchService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INetworkSwitch> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((networkSwitch: HttpResponse<NetworkSwitch>) => networkSwitch.body));
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

export const networkSwitchPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: NetworkSwitchDeletePopupComponent,
    resolve: {
      networkSwitch: NetworkSwitchResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.networkSwitch.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
