import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILaundryMachineProgram, LaundryMachineProgram } from '../laundry-machine-program.model';
import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';

@Injectable({ providedIn: 'root' })
export class LaundryMachineProgramRoutingResolveService implements Resolve<ILaundryMachineProgram> {
  constructor(protected service: LaundryMachineProgramService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILaundryMachineProgram> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((laundryMachineProgram: HttpResponse<LaundryMachineProgram>) => {
          if (laundryMachineProgram.body) {
            return of(laundryMachineProgram.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LaundryMachineProgram());
  }
}
