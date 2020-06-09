import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { GlobalSetting, IGlobalSetting } from 'app/shared/model/global-setting.model';
import { GlobalSettingService } from './global-setting.service';
import { GlobalSettingComponent } from './global-setting.component';
import { GlobalSettingDetailComponent } from './global-setting-detail.component';
import { GlobalSettingUpdateComponent } from './global-setting-update.component';

@Injectable({ providedIn: 'root' })
export class GlobalSettingResolve implements Resolve<IGlobalSetting> {
  constructor(private service: GlobalSettingService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGlobalSetting> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((globalSetting: HttpResponse<GlobalSetting>) => {
          if (globalSetting.body) {
            return of(globalSetting.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GlobalSetting());
  }
}

export const globalSettingRoute: Routes = [
  {
    path: '',
    component: GlobalSettingComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.globalSetting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GlobalSettingDetailComponent,
    resolve: {
      globalSetting: GlobalSettingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.globalSetting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GlobalSettingUpdateComponent,
    resolve: {
      globalSetting: GlobalSettingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.globalSetting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GlobalSettingUpdateComponent,
    resolve: {
      globalSetting: GlobalSettingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.globalSetting.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
