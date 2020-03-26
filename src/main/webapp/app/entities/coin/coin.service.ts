import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICoin } from 'app/shared/model/coin.model';

type EntityResponseType = HttpResponse<ICoin>;
type EntityArrayResponseType = HttpResponse<ICoin[]>;

@Injectable({ providedIn: 'root' })
export class CoinService {
  public resourceUrl = SERVER_API_URL + 'api/coins';

  constructor(protected http: HttpClient) {}

  create(coin: ICoin): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(coin);
    return this.http
      .post<ICoin>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(coin: ICoin): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(coin);
    return this.http
      .put<ICoin>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICoin>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICoin[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(coin: ICoin): ICoin {
    const copy: ICoin = Object.assign({}, coin, {
      datePurchase: coin.datePurchase && coin.datePurchase.isValid() ? coin.datePurchase.toJSON() : undefined,
      dateRedeem: coin.dateRedeem && coin.dateRedeem.isValid() ? coin.dateRedeem.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.datePurchase = res.body.datePurchase ? moment(res.body.datePurchase) : undefined;
      res.body.dateRedeem = res.body.dateRedeem ? moment(res.body.dateRedeem) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((coin: ICoin) => {
        coin.datePurchase = coin.datePurchase ? moment(coin.datePurchase) : undefined;
        coin.dateRedeem = coin.dateRedeem ? moment(coin.dateRedeem) : undefined;
      });
    }
    return res;
  }
}
