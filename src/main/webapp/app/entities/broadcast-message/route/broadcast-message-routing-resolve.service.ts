import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { BroadcastMessage, IBroadcastMessage } from '../broadcast-message.model';
import { BroadcastMessageService } from '../service/broadcast-message.service';

@Injectable({ providedIn: 'root' })
export class BroadcastMessageRoutingResolveService implements Resolve<IBroadcastMessage> {
  constructor(protected service: BroadcastMessageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBroadcastMessage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((broadcastMessage: HttpResponse<BroadcastMessage>) => {
          if (broadcastMessage.body) {
            return of(broadcastMessage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BroadcastMessage());
  }
}
