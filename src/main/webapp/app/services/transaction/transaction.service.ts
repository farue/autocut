import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from 'app/core/request/request-util';
import { Pagination } from 'app/core/request/request.model';
import { ITransactionOverview } from './transaction-overview.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  public resourceUrl = SERVER_API_URL + 'api/transaction-books';

  constructor(private http: HttpClient) {}

  query(req?: Pagination): Observable<HttpResponse<ITransactionOverview>> {
    const options = createRequestOption(req);
    return this.http.get<ITransactionOverview>(`${this.resourceUrl}/overview`, { params: options, observe: 'response' });
  }

  loadPurpose(): Observable<string> {
    return this.http.get(`${this.resourceUrl}/purpose`, { responseType: 'text' });
  }
}
