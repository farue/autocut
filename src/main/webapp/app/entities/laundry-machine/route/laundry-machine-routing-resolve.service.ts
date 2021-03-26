import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILaundryMachine, LaundryMachine } from '../laundry-machine.model';
import { LaundryMachineService } from '../service/laundry-machine.service';

@Injectable({ providedIn: 'root' })
export class LaundryMachineRoutingResolveService implements Resolve<ILaundryMachine> {
  constructor(protected service: LaundryMachineService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILaundryMachine> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((laundryMachine: HttpResponse<LaundryMachine>) => {
          if (laundryMachine.body) {
            return of(laundryMachine.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LaundryMachine());
  }
}
