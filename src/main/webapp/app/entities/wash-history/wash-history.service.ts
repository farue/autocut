import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
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
      date: washHistory.date && washHistory.date.isValid() ? washHistory.date.toJSON() : undefined,
      reservation: washHistory.reservation && washHistory.reservation.isValid() ? washHistory.reservation.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
      res.body.reservation = res.body.reservation ? moment(res.body.reservation) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((washHistory: IWashHistory) => {
        washHistory.date = washHistory.date ? moment(washHistory.date) : undefined;
        washHistory.reservation = washHistory.reservation ? moment(washHistory.reservation) : undefined;
      });
    }
    return res;
  }
}
