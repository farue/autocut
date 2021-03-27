import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGlobalSetting, GlobalSetting } from '../global-setting.model';
import { GlobalSettingService } from '../service/global-setting.service';

@Injectable({ providedIn: 'root' })
export class GlobalSettingRoutingResolveService implements Resolve<IGlobalSetting> {
  constructor(protected service: GlobalSettingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGlobalSetting> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((globalSetting: HttpResponse<GlobalSetting>) => {
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
