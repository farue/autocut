import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IBankTransaction } from 'app/shared/model/bank-transaction.model';

type EntityResponseType = HttpResponse<IBankTransaction>;
type EntityArrayResponseType = HttpResponse<IBankTransaction[]>;

@Injectable({ providedIn: 'root' })
export class BankTransactionService {
  public resourceUrl = SERVER_API_URL + 'api/bank-transactions';

  constructor(protected http: HttpClient) {}

  create(bankTransaction: IBankTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bankTransaction);
    return this.http
      .post<IBankTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(bankTransaction: IBankTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(bankTransaction);
    return this.http
      .put<IBankTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBankTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBankTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(bankTransaction: IBankTransaction): IBankTransaction {
    const copy: IBankTransaction = Object.assign({}, bankTransaction, {
      bookingDate: bankTransaction.bookingDate && bankTransaction.bookingDate.isValid() ? bankTransaction.bookingDate.toJSON() : undefined,
      valueDate: bankTransaction.valueDate && bankTransaction.valueDate.isValid() ? bankTransaction.valueDate.toJSON() : undefined,
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
      res.body.forEach((bankTransaction: IBankTransaction) => {
        bankTransaction.bookingDate = bankTransaction.bookingDate ? moment(bankTransaction.bookingDate) : undefined;
        bankTransaction.valueDate = bankTransaction.valueDate ? moment(bankTransaction.valueDate) : undefined;
      });
    }
    return res;
  }
}
