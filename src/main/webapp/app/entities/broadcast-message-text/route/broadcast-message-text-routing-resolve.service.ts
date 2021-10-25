import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { BroadcastMessageText, IBroadcastMessageText } from '../broadcast-message-text.model';
import { BroadcastMessageTextService } from '../service/broadcast-message-text.service';

@Injectable({ providedIn: 'root' })
export class BroadcastMessageTextRoutingResolveService implements Resolve<IBroadcastMessageText> {
  constructor(protected service: BroadcastMessageTextService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBroadcastMessageText> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((broadcastMessageText: HttpResponse<BroadcastMessageText>) => {
          if (broadcastMessageText.body) {
            return of(broadcastMessageText.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BroadcastMessageText());
  }
}
