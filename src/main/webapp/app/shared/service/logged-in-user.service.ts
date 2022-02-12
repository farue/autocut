import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from 'app/admin/user-management/user-management.model';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Tenant } from 'app/entities/tenant/tenant.model';
import { Lease } from 'app/entities/lease/lease.model';
import { WashHistory } from 'app/entities/wash-history/wash-history.model';
import { createRequestOption } from 'app/core/request/request-util';
import { Pagination } from 'app/core/request/request.model';
import { Program } from 'app/entities/washing/washing.model';
import { NetworkStatus } from 'app/entities/internet/network-status.model';
import { tap } from 'rxjs/operators';
import { dateToString, toDate } from 'app/core/util/date-util';
import { TransactionBook } from 'app/entities/transaction/transaction-book.model';
import dayjs from 'dayjs/esm';
import { InternalTransaction } from 'app/entities/internal-transaction/internal-transaction.model';
import { ApplicationConfigService } from '../../core/config/application-config.service';

@Injectable({ providedIn: 'root' })
export class LoggedInUserService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/me');

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  user(): Observable<User> {
    return this.http.get<User>(this.resourceUrl);
  }

  tenant(): Observable<Tenant> {
    return this.http.get<Tenant>(`${this.resourceUrl}/tenant`);
  }

  lease(): Observable<Lease> {
    return this.http.get<Lease>(`${this.resourceUrl}/lease`);
  }

  transactionBooks(): Observable<TransactionBook[]> {
    return this.http.get<TransactionBook[]>(`${this.resourceUrl}/transaction-books`);
  }

  transactions(id: number, from?: dayjs.Dayjs, until?: dayjs.Dayjs, req?: Pagination): Observable<HttpResponse<InternalTransaction[]>> {
    let options = createRequestOption(req);
    if (from) {
      options = options.set('from', dateToString(from));
    }
    if (until) {
      options = options.set('until', dateToString(until));
    }
    return this.http.get<InternalTransaction[]>(`${this.resourceUrl}/transaction-books/${id}/transactions`, {
      params: options,
      observe: 'response',
    });
  }

  washHistory(id?: number, req?: Pagination): Observable<HttpResponse<WashHistory[]>> {
    const options = createRequestOption(req);
    const url = `${this.resourceUrl}/laundry-machines/${id ? String(id) + '/' : ''}history`;
    return this.http.get<WashHistory[]>(url, { params: options, observe: 'response' });
  }

  washProgramSuggestions(id: number): Observable<Program[]> {
    return this.http.get<Program[]>(`${this.resourceUrl}/laundry-machines/${String(id)}/suggestions`);
  }

  networkStatus(): Observable<NetworkStatus> {
    return this.http.get<NetworkStatus>(`${this.resourceUrl}/network/status`).pipe(tap(res => (res.lastUpdate = toDate(res.lastUpdate))));
  }

  updateAndGetNetworkStatus(): Observable<NetworkStatus> {
    return this.http
      .post<NetworkStatus>(`${this.resourceUrl}/network/update`, null)
      .pipe(tap(res => (res.lastUpdate = toDate(res.lastUpdate))));
  }
}
