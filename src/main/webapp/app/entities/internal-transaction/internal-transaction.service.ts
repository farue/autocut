import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IInternalTransaction } from 'app/shared/model/internal-transaction.model';

type EntityResponseType = HttpResponse<IInternalTransaction>;
type EntityArrayResponseType = HttpResponse<IInternalTransaction[]>;

@Injectable({ providedIn: 'root' })
export class InternalTransactionService {
  public resourceUrl = SERVER_API_URL + 'api/internal-transactions';

  constructor(protected http: HttpClient) {}

  create(internalTransaction: IInternalTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(internalTransaction);
    return this.http
      .post<IInternalTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(internalTransaction: IInternalTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(internalTransaction);
    return this.http
      .put<IInternalTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IInternalTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IInternalTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(internalTransaction: IInternalTransaction): IInternalTransaction {
    const copy: IInternalTransaction = Object.assign({}, internalTransaction, {
      bookingDate:
        internalTransaction.bookingDate && internalTransaction.bookingDate.isValid() ? internalTransaction.bookingDate.toJSON() : undefined,
      valueDate:
        internalTransaction.valueDate && internalTransaction.valueDate.isValid() ? internalTransaction.valueDate.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.bookingDate = res.body.bookingDate ? moment(res.body.bookingDate) : undefined;
      res.body.valueDate = res.body.valueDate ? moment(res.body.valueDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((internalTransaction: IInternalTransaction) => {
        internalTransaction.bookingDate = internalTransaction.bookingDate ? moment(internalTransaction.bookingDate) : undefined;
        internalTransaction.valueDate = internalTransaction.valueDate ? moment(internalTransaction.valueDate) : undefined;
      });
    }
    return res;
  }
}
