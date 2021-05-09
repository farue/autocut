import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from 'app/core/request/request-util';
import { Pagination } from 'app/core/request/request.model';
import { ITransactionOverview } from './transaction-overview.model';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';
import { ITransaction } from 'app/entities/transaction/transaction.model';

export type EntityResponseType = HttpResponse<ITransactionOverview>;

@Injectable({ providedIn: 'root' })
export class TransactionService {
  public resourceUrl = SERVER_API_URL + 'api/transaction-books';

  constructor(private http: HttpClient) {}

  query(req?: Pagination): Observable<HttpResponse<ITransactionOverview>> {
    const options = createRequestOption(req);
    return this.http
      .get<ITransactionOverview>(`${this.resourceUrl}/overview`, { params: options, observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  loadPurpose(): Observable<string> {
    return this.http.get(`${this.resourceUrl}/purpose`, { responseType: 'text' });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body?.transactions) {
      res.body.transactions.forEach((transaction: ITransaction) => {
        transaction.bookingDate = transaction.bookingDate ? dayjs(transaction.bookingDate) : undefined;
        transaction.valueDate = transaction.valueDate ? dayjs(transaction.valueDate) : undefined;
      });
    }
    return res;
  }
}
