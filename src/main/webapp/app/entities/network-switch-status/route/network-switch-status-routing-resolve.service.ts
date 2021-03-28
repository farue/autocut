import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INetworkSwitchStatus, NetworkSwitchStatus } from '../network-switch-status.model';
import { NetworkSwitchStatusService } from '../service/network-switch-status.service';

@Injectable({ providedIn: 'root' })
export class NetworkSwitchStatusRoutingResolveService implements Resolve<INetworkSwitchStatus> {
  constructor(protected service: NetworkSwitchStatusService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INetworkSwitchStatus> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((networkSwitchStatus: HttpResponse<NetworkSwitchStatus>) => {
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
