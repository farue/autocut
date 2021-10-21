import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getInternetAccessIdentifier, IInternetAccess} from '../internet-access.model';

export type EntityResponseType = HttpResponse<IInternetAccess>;
export type EntityArrayResponseType = HttpResponse<IInternetAccess[]>;

@Injectable({ providedIn: 'root' })
export class InternetAccessService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/internet-accesses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(internetAccess: IInternetAccess): Observable<EntityResponseType> {
    return this.http.post<IInternetAccess>(this.resourceUrl, internetAccess, { observe: 'response' });
  }

  update(internetAccess: IInternetAccess): Observable<EntityResponseType> {
    return this.http.put<IInternetAccess>(`${this.resourceUrl}/${getInternetAccessIdentifier(internetAccess) as number}`, internetAccess, {
      observe: 'response',
    });
  }

  partialUpdate(internetAccess: IInternetAccess): Observable<EntityResponseType> {
    return this.http.patch<IInternetAccess>(
      `${this.resourceUrl}/${getInternetAccessIdentifier(internetAccess) as number}`,
      internetAccess,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IInternetAccess>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInternetAccess[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addInternetAccessToCollectionIfMissing(
    internetAccessCollection: IInternetAccess[],
    ...internetAccessesToCheck: (IInternetAccess | null | undefined)[]
  ): IInternetAccess[] {
    const internetAccesses: IInternetAccess[] = internetAccessesToCheck.filter(isPresent);
    if (internetAccesses.length > 0) {
      const internetAccessCollectionIdentifiers = internetAccessCollection.map(
        internetAccessItem => getInternetAccessIdentifier(internetAccessItem)!
      );
      const internetAccessesToAdd = internetAccesses.filter(internetAccessItem => {
        const internetAccessIdentifier = getInternetAccessIdentifier(internetAccessItem);
        if (internetAccessIdentifier == null || internetAccessCollectionIdentifiers.includes(internetAccessIdentifier)) {
          return false;
        }
        internetAccessCollectionIdentifiers.push(internetAccessIdentifier);
        return true;
      });
      return [...internetAccessesToAdd, ...internetAccessCollection];
    }
    return internetAccessCollection;
  }
}
