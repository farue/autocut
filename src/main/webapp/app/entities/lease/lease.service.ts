import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

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

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(lease: ILease): ILease {
    const copy: ILease = Object.assign({}, lease, {
      start: lease.start && lease.start.isValid() ? lease.start.toJSON() : undefined,
      end: lease.end && lease.end.isValid() ? lease.end.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.start = res.body.start ? moment(res.body.start) : undefined;
      res.body.end = res.body.end ? moment(res.body.end) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((lease: ILease) => {
        lease.start = lease.start ? moment(lease.start) : undefined;
        lease.end = lease.end ? moment(lease.end) : undefined;
      });
    }
    return res;
  }
}
