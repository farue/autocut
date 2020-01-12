import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IPaymentAccount } from 'app/shared/model/payment-account.model';

type EntityResponseType = HttpResponse<IPaymentAccount>;
type EntityArrayResponseType = HttpResponse<IPaymentAccount[]>;

@Injectable({ providedIn: 'root' })
export class PaymentAccountService {
  public resourceUrl = SERVER_API_URL + 'api/payment-accounts';

  constructor(protected http: HttpClient) {}

  create(paymentAccount: IPaymentAccount): Observable<EntityResponseType> {
    return this.http.post<IPaymentAccount>(this.resourceUrl, paymentAccount, { observe: 'response' });
  }

  update(paymentAccount: IPaymentAccount): Observable<EntityResponseType> {
    return this.http.put<IPaymentAccount>(this.resourceUrl, paymentAccount, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPaymentAccount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPaymentAccount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
