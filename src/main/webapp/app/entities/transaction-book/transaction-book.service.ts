import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITransactionBook } from 'app/shared/model/transaction-book.model';

type EntityResponseType = HttpResponse<ITransactionBook>;
type EntityArrayResponseType = HttpResponse<ITransactionBook[]>;

@Injectable({ providedIn: 'root' })
export class TransactionBookService {
  public resourceUrl = SERVER_API_URL + 'api/transaction-books';

  constructor(protected http: HttpClient) {}

  create(transactionBook: ITransactionBook): Observable<EntityResponseType> {
    return this.http.post<ITransactionBook>(this.resourceUrl, transactionBook, { observe: 'response' });
  }

  update(transactionBook: ITransactionBook): Observable<EntityResponseType> {
    return this.http.put<ITransactionBook>(this.resourceUrl, transactionBook, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITransactionBook>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransactionBook[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
