import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ILease } from 'app/shared/model/lease.model';

type EntityResponseType = HttpResponse<ILease>;
type EntityArrayResponseType = HttpResponse<ILease[]>;

@Injectable({ providedIn: 'root' })
export class LeaseService {
  public resourceUrl = SERVER_API_URL + 'api/leases';

  constructor(protected http: HttpClient) {}

  create(lease: ILease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lease);
    return this.http
      .post<ILease>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(lease: ILease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lease);
    return this.http
      .put<ILease>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILease>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILease[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(lease: ILease): ILease {
    const copy: ILease = Object.assign({}, lease, {
      start: lease.start != null && lease.start.isValid() ? lease.start.toJSON() : null,
      end: lease.end != null && lease.end.isValid() ? lease.end.toJSON() : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.start = res.body.start != null ? moment(res.body.start) : null;
      res.body.end = res.body.end != null ? moment(res.body.end) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((lease: ILease) => {
        lease.start = lease.start != null ? moment(lease.start) : null;
        lease.end = lease.end != null ? moment(lease.end) : null;
      });
    }
    return res;
  }
}
