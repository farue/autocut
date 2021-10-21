import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getLaundryMachineIdentifier, ILaundryMachine} from '../laundry-machine.model';

export type EntityResponseType = HttpResponse<ILaundryMachine>;
export type EntityArrayResponseType = HttpResponse<ILaundryMachine[]>;

@Injectable({ providedIn: 'root' })
export class LaundryMachineService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/laundry-machines');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(laundryMachine: ILaundryMachine): Observable<EntityResponseType> {
    return this.http.post<ILaundryMachine>(this.resourceUrl, laundryMachine, { observe: 'response' });
  }

  update(laundryMachine: ILaundryMachine): Observable<EntityResponseType> {
    return this.http.put<ILaundryMachine>(`${this.resourceUrl}/${getLaundryMachineIdentifier(laundryMachine) as number}`, laundryMachine, {
      observe: 'response',
    });
  }

  partialUpdate(laundryMachine: ILaundryMachine): Observable<EntityResponseType> {
    return this.http.patch<ILaundryMachine>(
      `${this.resourceUrl}/${getLaundryMachineIdentifier(laundryMachine) as number}`,
      laundryMachine,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILaundryMachine>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILaundryMachine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLaundryMachineToCollectionIfMissing(
    laundryMachineCollection: ILaundryMachine[],
    ...laundryMachinesToCheck: (ILaundryMachine | null | undefined)[]
  ): ILaundryMachine[] {
    const laundryMachines: ILaundryMachine[] = laundryMachinesToCheck.filter(isPresent);
    if (laundryMachines.length > 0) {
      const laundryMachineCollectionIdentifiers = laundryMachineCollection.map(
        laundryMachineItem => getLaundryMachineIdentifier(laundryMachineItem)!
      );
      const laundryMachinesToAdd = laundryMachines.filter(laundryMachineItem => {
        const laundryMachineIdentifier = getLaundryMachineIdentifier(laundryMachineItem);
        if (laundryMachineIdentifier == null || laundryMachineCollectionIdentifiers.includes(laundryMachineIdentifier)) {
          return false;
        }
        laundryMachineCollectionIdentifiers.push(laundryMachineIdentifier);
        return true;
      });
      return [...laundryMachinesToAdd, ...laundryMachineCollection];
    }
    return laundryMachineCollection;
  }
}
