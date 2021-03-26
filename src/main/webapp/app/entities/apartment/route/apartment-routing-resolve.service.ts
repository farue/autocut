import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IApartment, Apartment } from '../apartment.model';
import { ApartmentService } from '../service/apartment.service';

@Injectable({ providedIn: 'root' })
export class ApartmentRoutingResolveService implements Resolve<IApartment> {
  constructor(protected service: ApartmentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IApartment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((apartment: HttpResponse<Apartment>) => {
          if (apartment.body) {
            return of(apartment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Apartment());
  }
}
