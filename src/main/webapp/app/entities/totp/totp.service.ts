import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITotp } from 'app/shared/model/totp.model';

type EntityResponseType = HttpResponse<ITotp>;
type EntityArrayResponseType = HttpResponse<ITotp[]>;

@Injectable({ providedIn: 'root' })
export class TotpService {
  public resourceUrl = SERVER_API_URL + 'api/totps';

  constructor(protected http: HttpClient) {}

  create(totp: ITotp): Observable<EntityResponseType> {
    return this.http.post<ITotp>(this.resourceUrl, totp, { observe: 'response' });
  }

  update(totp: ITotp): Observable<EntityResponseType> {
    return this.http.put<ITotp>(this.resourceUrl, totp, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITotp>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITotp[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
