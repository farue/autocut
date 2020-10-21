import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../../core/user/user.model';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { Tenant } from '../model/tenant.model';
import { Lease } from '../model/lease.model';
import { TransactionBook } from '../model/transaction-book.model';

@Injectable({ providedIn: 'root' })
export class LoggedInUserService {
  public resourceUrl = SERVER_API_URL + 'api/me';

  constructor(private http: HttpClient) {}

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
}
