import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GlobalSettingComponent } from '../list/global-setting.component';
import { GlobalSettingDetailComponent } from '../detail/global-setting-detail.component';
import { GlobalSettingUpdateComponent } from '../update/global-setting-update.component';
import { GlobalSettingRoutingResolveService } from './global-setting-routing-resolve.service';

const globalSettingRoute: Routes = [
  {
    path: '',
    component: GlobalSettingComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GlobalSettingDetailComponent,
    resolve: {
      globalSetting: GlobalSettingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GlobalSettingUpdateComponent,
    resolve: {
      globalSetting: GlobalSettingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GlobalSettingUpdateComponent,
    resolve: {
      globalSetting: GlobalSettingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(globalSettingRoute)],
  exports: [RouterModule],
})
export class GlobalSettingRoutingModule {}
