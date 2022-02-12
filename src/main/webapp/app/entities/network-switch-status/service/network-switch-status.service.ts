import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getNetworkSwitchStatusIdentifier, INetworkSwitchStatus } from '../network-switch-status.model';

export type EntityResponseType = HttpResponse<INetworkSwitchStatus>;
export type EntityArrayResponseType = HttpResponse<INetworkSwitchStatus[]>;

@Injectable({ providedIn: 'root' })
export class NetworkSwitchStatusService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/network-switch-statuses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(networkSwitchStatus: INetworkSwitchStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(networkSwitchStatus);
    return this.http
      .post<INetworkSwitchStatus>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(networkSwitchStatus: INetworkSwitchStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(networkSwitchStatus);
    return this.http
      .put<INetworkSwitchStatus>(`${this.resourceUrl}/${getNetworkSwitchStatusIdentifier(networkSwitchStatus) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(networkSwitchStatus: INetworkSwitchStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(networkSwitchStatus);
    return this.http
      .patch<INetworkSwitchStatus>(`${this.resourceUrl}/${getNetworkSwitchStatusIdentifier(networkSwitchStatus) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INetworkSwitchStatus>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INetworkSwitchStatus[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNetworkSwitchStatusToCollectionIfMissing(
    networkSwitchStatusCollection: INetworkSwitchStatus[],
    ...networkSwitchStatusesToCheck: (INetworkSwitchStatus | null | undefined)[]
  ): INetworkSwitchStatus[] {
    const networkSwitchStatuses: INetworkSwitchStatus[] = networkSwitchStatusesToCheck.filter(isPresent);
    if (networkSwitchStatuses.length > 0) {
      const networkSwitchStatusCollectionIdentifiers = networkSwitchStatusCollection.map(
        networkSwitchStatusItem => getNetworkSwitchStatusIdentifier(networkSwitchStatusItem)!
      );
      const networkSwitchStatusesToAdd = networkSwitchStatuses.filter(networkSwitchStatusItem => {
        const networkSwitchStatusIdentifier = getNetworkSwitchStatusIdentifier(networkSwitchStatusItem);
        if (networkSwitchStatusIdentifier == null || networkSwitchStatusCollectionIdentifiers.includes(networkSwitchStatusIdentifier)) {
          return false;
        }
        networkSwitchStatusCollectionIdentifiers.push(networkSwitchStatusIdentifier);
        return true;
      });
      return [...networkSwitchStatusesToAdd, ...networkSwitchStatusCollection];
    }
    return networkSwitchStatusCollection;
  }

  protected convertDateFromClient(networkSwitchStatus: INetworkSwitchStatus): INetworkSwitchStatus {
    return Object.assign({}, networkSwitchStatus, {
      timestamp: networkSwitchStatus.timestamp?.isValid() ? networkSwitchStatus.timestamp.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.timestamp = res.body.timestamp ? dayjs(res.body.timestamp) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((networkSwitchStatus: INetworkSwitchStatus) => {
        networkSwitchStatus.timestamp = networkSwitchStatus.timestamp ? dayjs(networkSwitchStatus.timestamp) : undefined;
      });
    }
    return res;
  }
}
