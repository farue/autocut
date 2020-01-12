import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITenant } from 'app/shared/model/tenant.model';

type EntityResponseType = HttpResponse<ITenant>;
type EntityArrayResponseType = HttpResponse<ITenant[]>;

@Injectable({ providedIn: 'root' })
export class TenantService {
  public resourceUrl = SERVER_API_URL + 'api/tenants';

  constructor(protected http: HttpClient) {}

  create(tenant: ITenant): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tenant);
    return this.http
      .post<ITenant>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(tenant: ITenant): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tenant);
    return this.http
      .put<ITenant>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITenant>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITenant[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(tenant: ITenant): ITenant {
    const copy: ITenant = Object.assign({}, tenant, {
      createdDate: tenant.createdDate && tenant.createdDate.isValid() ? tenant.createdDate.toJSON() : undefined,
      lastModifiedDate: tenant.lastModifiedDate && tenant.lastModifiedDate.isValid() ? tenant.lastModifiedDate.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? moment(res.body.createdDate) : undefined;
      res.body.lastModifiedDate = res.body.lastModifiedDate ? moment(res.body.lastModifiedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((tenant: ITenant) => {
        tenant.createdDate = tenant.createdDate ? moment(tenant.createdDate) : undefined;
        tenant.lastModifiedDate = tenant.lastModifiedDate ? moment(tenant.lastModifiedDate) : undefined;
      });
    }
    return res;
  }
}
