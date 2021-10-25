import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getLaundryMachineProgramIdentifier, ILaundryMachineProgram } from '../laundry-machine-program.model';

export type EntityResponseType = HttpResponse<ILaundryMachineProgram>;
export type EntityArrayResponseType = HttpResponse<ILaundryMachineProgram[]>;

@Injectable({ providedIn: 'root' })
export class LaundryMachineProgramService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/laundry-machine-programs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(laundryMachineProgram: ILaundryMachineProgram): Observable<EntityResponseType> {
    return this.http.post<ILaundryMachineProgram>(this.resourceUrl, laundryMachineProgram, { observe: 'response' });
  }

  update(laundryMachineProgram: ILaundryMachineProgram): Observable<EntityResponseType> {
    return this.http.put<ILaundryMachineProgram>(
      `${this.resourceUrl}/${getLaundryMachineProgramIdentifier(laundryMachineProgram) as number}`,
      laundryMachineProgram,
      { observe: 'response' }
    );
  }

  partialUpdate(laundryMachineProgram: ILaundryMachineProgram): Observable<EntityResponseType> {
    return this.http.patch<ILaundryMachineProgram>(
      `${this.resourceUrl}/${getLaundryMachineProgramIdentifier(laundryMachineProgram) as number}`,
      laundryMachineProgram,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILaundryMachineProgram>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILaundryMachineProgram[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLaundryMachineProgramToCollectionIfMissing(
    laundryMachineProgramCollection: ILaundryMachineProgram[],
    ...laundryMachineProgramsToCheck: (ILaundryMachineProgram | null | undefined)[]
  ): ILaundryMachineProgram[] {
    const laundryMachinePrograms: ILaundryMachineProgram[] = laundryMachineProgramsToCheck.filter(isPresent);
    if (laundryMachinePrograms.length > 0) {
      const laundryMachineProgramCollectionIdentifiers = laundryMachineProgramCollection.map(
        laundryMachineProgramItem => getLaundryMachineProgramIdentifier(laundryMachineProgramItem)!
      );
      const laundryMachineProgramsToAdd = laundryMachinePrograms.filter(laundryMachineProgramItem => {
        const laundryMachineProgramIdentifier = getLaundryMachineProgramIdentifier(laundryMachineProgramItem);
        if (
          laundryMachineProgramIdentifier == null ||
          laundryMachineProgramCollectionIdentifiers.includes(laundryMachineProgramIdentifier)
        ) {
          return false;
        }
        laundryMachineProgramCollectionIdentifiers.push(laundryMachineProgramIdentifier);
        return true;
      });
      return [...laundryMachineProgramsToAdd, ...laundryMachineProgramCollection];
    }
    return laundryMachineProgramCollection;
  }
}
