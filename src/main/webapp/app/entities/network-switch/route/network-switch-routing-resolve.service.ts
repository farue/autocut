import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INetworkSwitch, NetworkSwitch } from '../network-switch.model';
import { NetworkSwitchService } from '../service/network-switch.service';

@Injectable({ providedIn: 'root' })
export class NetworkSwitchRoutingResolveService implements Resolve<INetworkSwitch> {
  constructor(protected service: NetworkSwitchService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INetworkSwitch> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((networkSwitch: HttpResponse<NetworkSwitch>) => {
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
