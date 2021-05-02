import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IApartment, getApartmentIdentifier } from '../apartment.model';

export type EntityResponseType = HttpResponse<IApartment>;
export type EntityArrayResponseType = HttpResponse<IApartment[]>;

@Injectable({ providedIn: 'root' })
export class ApartmentService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/apartments');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(apartment: IApartment): Observable<EntityResponseType> {
    return this.http.post<IApartment>(this.resourceUrl, apartment, { observe: 'response' });
  }

  update(apartment: IApartment): Observable<EntityResponseType> {
    return this.http.put<IApartment>(`${this.resourceUrl}/${getApartmentIdentifier(apartment) as number}`, apartment, {
      observe: 'response',
    });
  }

  partialUpdate(apartment: IApartment): Observable<EntityResponseType> {
    return this.http.patch<IApartment>(`${this.resourceUrl}/${getApartmentIdentifier(apartment) as number}`, apartment, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IApartment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IApartment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addApartmentToCollectionIfMissing(
    apartmentCollection: IApartment[],
    ...apartmentsToCheck: (IApartment | null | undefined)[]
  ): IApartment[] {
    const apartments: IApartment[] = apartmentsToCheck.filter(isPresent);
    if (apartments.length > 0) {
      const apartmentCollectionIdentifiers = apartmentCollection.map(apartmentItem => getApartmentIdentifier(apartmentItem)!);
      const apartmentsToAdd = apartments.filter(apartmentItem => {
        const apartmentIdentifier = getApartmentIdentifier(apartmentItem);
        if (apartmentIdentifier == null || apartmentCollectionIdentifiers.includes(apartmentIdentifier)) {
          return false;
        }
        apartmentCollectionIdentifiers.push(apartmentIdentifier);
        return true;
      });
      return [...apartmentsToAdd, ...apartmentCollection];
    }
    return apartmentCollection;
  }

  getByNr(nr: string): Observable<EntityResponseType> {
    return this.http.get<IApartment>(`${this.resourceUrl}/nr=${nr}`, { observe: 'response' });
  }
}
