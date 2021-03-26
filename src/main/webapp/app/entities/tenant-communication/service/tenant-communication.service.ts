import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITenantCommunication, getTenantCommunicationIdentifier } from '../tenant-communication.model';

export type EntityResponseType = HttpResponse<ITenantCommunication>;
export type EntityArrayResponseType = HttpResponse<ITenantCommunication[]>;

@Injectable({ providedIn: 'root' })
export class TenantCommunicationService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/tenant-communications');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(tenantCommunication: ITenantCommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tenantCommunication);
    return this.http
      .post<ITenantCommunication>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(tenantCommunication: ITenantCommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tenantCommunication);
    return this.http
      .put<ITenantCommunication>(`${this.resourceUrl}/${getTenantCommunicationIdentifier(tenantCommunication) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(tenantCommunication: ITenantCommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tenantCommunication);
    return this.http
      .patch<ITenantCommunication>(`${this.resourceUrl}/${getTenantCommunicationIdentifier(tenantCommunication) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITenantCommunication>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITenantCommunication[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTenantCommunicationToCollectionIfMissing(
    tenantCommunicationCollection: ITenantCommunication[],
    ...tenantCommunicationsToCheck: (ITenantCommunication | null | undefined)[]
  ): ITenantCommunication[] {
    const tenantCommunications: ITenantCommunication[] = tenantCommunicationsToCheck.filter(isPresent);
    if (tenantCommunications.length > 0) {
      const tenantCommunicationCollectionIdentifiers = tenantCommunicationCollection.map(
        tenantCommunicationItem => getTenantCommunicationIdentifier(tenantCommunicationItem)!
      );
      const tenantCommunicationsToAdd = tenantCommunications.filter(tenantCommunicationItem => {
        const tenantCommunicationIdentifier = getTenantCommunicationIdentifier(tenantCommunicationItem);
        if (tenantCommunicationIdentifier == null || tenantCommunicationCollectionIdentifiers.includes(tenantCommunicationIdentifier)) {
          return false;
        }
        tenantCommunicationCollectionIdentifiers.push(tenantCommunicationIdentifier);
        return true;
      });
      return [...tenantCommunicationsToAdd, ...tenantCommunicationCollection];
    }
    return tenantCommunicationCollection;
  }

  protected convertDateFromClient(tenantCommunication: ITenantCommunication): ITenantCommunication {
    return Object.assign({}, tenantCommunication, {
      date: tenantCommunication.date?.isValid() ? tenantCommunication.date.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((tenantCommunication: ITenantCommunication) => {
        tenantCommunication.date = tenantCommunication.date ? dayjs(tenantCommunication.date) : undefined;
      });
    }
    return res;
  }
}
