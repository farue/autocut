import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getNetworkSwitchIdentifier, INetworkSwitch} from '../network-switch.model';

export type EntityResponseType = HttpResponse<INetworkSwitch>;
export type EntityArrayResponseType = HttpResponse<INetworkSwitch[]>;

@Injectable({ providedIn: 'root' })
export class NetworkSwitchService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/network-switches');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(networkSwitch: INetworkSwitch): Observable<EntityResponseType> {
    return this.http.post<INetworkSwitch>(this.resourceUrl, networkSwitch, { observe: 'response' });
  }

  update(networkSwitch: INetworkSwitch): Observable<EntityResponseType> {
    return this.http.put<INetworkSwitch>(`${this.resourceUrl}/${getNetworkSwitchIdentifier(networkSwitch) as number}`, networkSwitch, {
      observe: 'response',
    });
  }

  partialUpdate(networkSwitch: INetworkSwitch): Observable<EntityResponseType> {
    return this.http.patch<INetworkSwitch>(`${this.resourceUrl}/${getNetworkSwitchIdentifier(networkSwitch) as number}`, networkSwitch, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INetworkSwitch>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INetworkSwitch[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNetworkSwitchToCollectionIfMissing(
    networkSwitchCollection: INetworkSwitch[],
    ...networkSwitchesToCheck: (INetworkSwitch | null | undefined)[]
  ): INetworkSwitch[] {
    const networkSwitches: INetworkSwitch[] = networkSwitchesToCheck.filter(isPresent);
    if (networkSwitches.length > 0) {
      const networkSwitchCollectionIdentifiers = networkSwitchCollection.map(
        networkSwitchItem => getNetworkSwitchIdentifier(networkSwitchItem)!
      );
      const networkSwitchesToAdd = networkSwitches.filter(networkSwitchItem => {
        const networkSwitchIdentifier = getNetworkSwitchIdentifier(networkSwitchItem);
        if (networkSwitchIdentifier == null || networkSwitchCollectionIdentifiers.includes(networkSwitchIdentifier)) {
          return false;
        }
        networkSwitchCollectionIdentifiers.push(networkSwitchIdentifier);
        return true;
      });
      return [...networkSwitchesToAdd, ...networkSwitchCollection];
    }
    return networkSwitchCollection;
  }
}
