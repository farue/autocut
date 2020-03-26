import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ICoin, Coin } from 'app/shared/model/coin.model';
import { CoinService } from './coin.service';
import { CoinComponent } from './coin.component';
import { CoinDetailComponent } from './coin-detail.component';
import { CoinUpdateComponent } from './coin-update.component';

@Injectable({ providedIn: 'root' })
export class CoinResolve implements Resolve<ICoin> {
  constructor(private service: CoinService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICoin> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((coin: HttpResponse<Coin>) => {
          if (coin.body) {
            return of(coin.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Coin());
  }
}

export const coinRoute: Routes = [
  {
    path: '',
    component: CoinComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.coin.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: CoinDetailComponent,
    resolve: {
      coin: CoinResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.coin.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: CoinUpdateComponent,
    resolve: {
      coin: CoinResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.coin.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: CoinUpdateComponent,
    resolve: {
      coin: CoinResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.coin.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
