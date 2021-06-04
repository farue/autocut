import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getLaundryProgramIdentifier, ILaundryProgram } from '../laundry-program.model';

export type EntityResponseType = HttpResponse<ILaundryProgram>;
export type EntityArrayResponseType = HttpResponse<ILaundryProgram[]>;

@Injectable({ providedIn: 'root' })
export class LaundryProgramService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/laundry-programs');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(laundryProgram: ILaundryProgram): Observable<EntityResponseType> {
    return this.http.post<ILaundryProgram>(this.resourceUrl, laundryProgram, { observe: 'response' });
  }

  update(laundryProgram: ILaundryProgram): Observable<EntityResponseType> {
    return this.http.put<ILaundryProgram>(`${this.resourceUrl}/${getLaundryProgramIdentifier(laundryProgram) as number}`, laundryProgram, {
      observe: 'response',
    });
  }

  partialUpdate(laundryProgram: ILaundryProgram): Observable<EntityResponseType> {
    return this.http.patch<ILaundryProgram>(
      `${this.resourceUrl}/${getLaundryProgramIdentifier(laundryProgram) as number}`,
      laundryProgram,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILaundryProgram>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILaundryProgram[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLaundryProgramToCollectionIfMissing(
    laundryProgramCollection: ILaundryProgram[],
    ...laundryProgramsToCheck: (ILaundryProgram | null | undefined)[]
  ): ILaundryProgram[] {
    const laundryPrograms: ILaundryProgram[] = laundryProgramsToCheck.filter(isPresent);
    if (laundryPrograms.length > 0) {
      const laundryProgramCollectionIdentifiers = laundryProgramCollection.map(
        laundryProgramItem => getLaundryProgramIdentifier(laundryProgramItem)!
      );
      const laundryProgramsToAdd = laundryPrograms.filter(laundryProgramItem => {
        const laundryProgramIdentifier = getLaundryProgramIdentifier(laundryProgramItem);
        if (laundryProgramIdentifier == null || laundryProgramCollectionIdentifiers.includes(laundryProgramIdentifier)) {
          return false;
        }
        laundryProgramCollectionIdentifiers.push(laundryProgramIdentifier);
        return true;
      });
      return [...laundryProgramsToAdd, ...laundryProgramCollection];
    }
    return laundryProgramCollection;
  }
}
