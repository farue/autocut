import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';

type EntityResponseType = HttpResponse<ITenantCommunication>;
type EntityArrayResponseType = HttpResponse<ITenantCommunication[]>;

@Injectable({ providedIn: 'root' })
export class TenantCommunicationService {
  public resourceUrl = SERVER_API_URL + 'api/tenant-communications';

  constructor(protected http: HttpClient) {}

  create(tenantCommunication: ITenantCommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tenantCommunication);
    return this.http
      .post<ITenantCommunication>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(tenantCommunication: ITenantCommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tenantCommunication);
    return this.http
      .put<ITenantCommunication>(this.resourceUrl, copy, { observe: 'response' })
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

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(tenantCommunication: ITenantCommunication): ITenantCommunication {
    const copy: ITenantCommunication = Object.assign({}, tenantCommunication, {
      date: tenantCommunication.date != null && tenantCommunication.date.isValid() ? tenantCommunication.date.toJSON() : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date != null ? moment(res.body.date) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((tenantCommunication: ITenantCommunication) => {
        tenantCommunication.date = tenantCommunication.date != null ? moment(tenantCommunication.date) : null;
      });
    }
    return res;
  }
}
