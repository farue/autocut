import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IPort, Port } from 'app/shared/model/port.model';
import { PortService } from './port.service';
import { PortComponent } from './port.component';
import { PortDetailComponent } from './port-detail.component';
import { PortUpdateComponent } from './port-update.component';
import { PortDeletePopupComponent } from './port-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class PortResolve implements Resolve<IPort> {
  constructor(private service: PortService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPort> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((port: HttpResponse<Port>) => port.body));
    }
    return of(new Port());
  }
}

export const portRoute: Routes = [
  {
    path: '',
    component: PortComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.port.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PortDetailComponent,
    resolve: {
      port: PortResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.port.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PortUpdateComponent,
    resolve: {
      port: PortResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.port.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PortUpdateComponent,
    resolve: {
      port: PortResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.port.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const portPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: PortDeletePopupComponent,
    resolve: {
      port: PortResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.port.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
