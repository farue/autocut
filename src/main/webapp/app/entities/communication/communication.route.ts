import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Communication, ICommunication } from 'app/shared/model/communication.model';
import { CommunicationService } from './communication.service';
import { CommunicationComponent } from './communication.component';
import { CommunicationDetailComponent } from './communication-detail.component';
import { CommunicationUpdateComponent } from './communication-update.component';

@Injectable({ providedIn: 'root' })
export class CommunicationResolve implements Resolve<ICommunication> {
  constructor(private service: CommunicationService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICommunication> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((communication: HttpResponse<Communication>) => {
          if (communication.body) {
            return of(communication.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Communication());
  }
}

export const communicationRoute: Routes = [
  {
    path: '',
    component: CommunicationComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.communication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CommunicationDetailComponent,
    resolve: {
      communication: CommunicationResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.communication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CommunicationUpdateComponent,
    resolve: {
      communication: CommunicationResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.communication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CommunicationUpdateComponent,
    resolve: {
      communication: CommunicationResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.communication.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
