import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IPaymentEntry } from 'app/shared/model/payment-entry.model';

type EntityResponseType = HttpResponse<IPaymentEntry>;
type EntityArrayResponseType = HttpResponse<IPaymentEntry[]>;

@Injectable({ providedIn: 'root' })
export class PaymentEntryService {
  public resourceUrl = SERVER_API_URL + 'api/payment-entries';

  constructor(protected http: HttpClient) {}

  create(paymentEntry: IPaymentEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentEntry);
    return this.http
      .post<IPaymentEntry>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(paymentEntry: IPaymentEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentEntry);
    return this.http
      .put<IPaymentEntry>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPaymentEntry>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPaymentEntry[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(paymentEntry: IPaymentEntry): IPaymentEntry {
    const copy: IPaymentEntry = Object.assign({}, paymentEntry, {
      date: paymentEntry.date && paymentEntry.date.isValid() ? paymentEntry.date.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((paymentEntry: IPaymentEntry) => {
        paymentEntry.date = paymentEntry.date ? moment(paymentEntry.date) : undefined;
      });
    }
    return res;
  }
}
