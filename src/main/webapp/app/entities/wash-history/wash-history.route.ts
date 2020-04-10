import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IWashHistory, WashHistory } from 'app/shared/model/wash-history.model';
import { WashHistoryService } from './wash-history.service';
import { WashHistoryComponent } from './wash-history.component';
import { WashHistoryDetailComponent } from './wash-history-detail.component';
import { WashHistoryUpdateComponent } from './wash-history-update.component';

@Injectable({ providedIn: 'root' })
export class WashHistoryResolve implements Resolve<IWashHistory> {
  constructor(private service: WashHistoryService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWashHistory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((washHistory: HttpResponse<WashHistory>) => {
          if (washHistory.body) {
            return of(washHistory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new WashHistory());
  }
}

export const washHistoryRoute: Routes = [
  {
    path: '',
    component: WashHistoryComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.washHistory.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: WashHistoryDetailComponent,
    resolve: {
      washHistory: WashHistoryResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.washHistory.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: WashHistoryUpdateComponent,
    resolve: {
      washHistory: WashHistoryResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.washHistory.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: WashHistoryUpdateComponent,
    resolve: {
      washHistory: WashHistoryResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.washHistory.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
