import { AbstractControl, AsyncValidator, ValidationErrors } from '@angular/forms';
import { Injectable } from '@angular/core';
import { ApartmentService, EntityResponseType } from 'app/entities/apartment/service/apartment.service';
import { Observable, of } from 'rxjs';
import { catchError, first, map, tap } from 'rxjs/operators';
import { IApartment } from 'app/entities/apartment/apartment.model';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApartmentValidator implements AsyncValidator {
  apartment: IApartment | null = null;

  constructor(private apartmentService: ApartmentService) {}

  validate(control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
    this.apartment = null;
    return this.apartmentService.getByNr(control.value).pipe(
      first(),
      map((response: EntityResponseType) => response.body),
      tap((apartment: IApartment | null) => (this.apartment = apartment)),
      map((apartment: IApartment | null) => (apartment == null ? { invalidApartment: true } : null)),
      catchError((err: HttpErrorResponse) => {
        if (err.status === 404) {
          return of({ invalidApartment: true });
        }
        return of(null);
      })
    );
  }
}
