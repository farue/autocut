import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IWashHistory } from 'app/shared/model/wash-history.model';

type EntityResponseType = HttpResponse<IWashHistory>;
type EntityArrayResponseType = HttpResponse<IWashHistory[]>;

@Injectable({ providedIn: 'root' })
export class WashHistoryService {
  public resourceUrl = SERVER_API_URL + 'api/wash-histories';

  constructor(protected http: HttpClient) {}

  create(washHistory: IWashHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(washHistory);
    return this.http
      .post<IWashHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(washHistory: IWashHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(washHistory);
    return this.http
      .put<IWashHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IWashHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IWashHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(washHistory: IWashHistory): IWashHistory {
    const copy: IWashHistory = Object.assign({}, washHistory, {
      usingDate: washHistory.usingDate && washHistory.usingDate.isValid() ? washHistory.usingDate.toJSON() : undefined,
      reservationDate:
        washHistory.reservationDate && washHistory.reservationDate.isValid() ? washHistory.reservationDate.toJSON() : undefined,
      lastModifiedDate:
        washHistory.lastModifiedDate && washHistory.lastModifiedDate.isValid() ? washHistory.lastModifiedDate.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.usingDate = res.body.usingDate ? moment(res.body.usingDate) : undefined;
      res.body.reservationDate = res.body.reservationDate ? moment(res.body.reservationDate) : undefined;
      res.body.lastModifiedDate = res.body.lastModifiedDate ? moment(res.body.lastModifiedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((washHistory: IWashHistory) => {
        washHistory.usingDate = washHistory.usingDate ? moment(washHistory.usingDate) : undefined;
        washHistory.reservationDate = washHistory.reservationDate ? moment(washHistory.reservationDate) : undefined;
        washHistory.lastModifiedDate = washHistory.lastModifiedDate ? moment(washHistory.lastModifiedDate) : undefined;
      });
    }
    return res;
  }
}
