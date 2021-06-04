import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILaundryProgram, LaundryProgram } from '../laundry-program.model';
import { LaundryProgramService } from '../service/laundry-program.service';

@Injectable({ providedIn: 'root' })
export class LaundryProgramRoutingResolveService implements Resolve<ILaundryProgram> {
  constructor(protected service: LaundryProgramService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILaundryProgram> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((laundryProgram: HttpResponse<LaundryProgram>) => {
          if (laundryProgram.body) {
            return of(laundryProgram.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LaundryProgram());
  }
}
