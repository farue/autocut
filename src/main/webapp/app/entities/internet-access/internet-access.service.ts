import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IInternetAccess } from 'app/shared/model/internet-access.model';

type EntityResponseType = HttpResponse<IInternetAccess>;
type EntityArrayResponseType = HttpResponse<IInternetAccess[]>;

@Injectable({ providedIn: 'root' })
export class InternetAccessService {
  public resourceUrl = SERVER_API_URL + 'api/internet-accesses';

  constructor(protected http: HttpClient) {}

  create(internetAccess: IInternetAccess): Observable<EntityResponseType> {
    return this.http.post<IInternetAccess>(this.resourceUrl, internetAccess, { observe: 'response' });
  }

  update(internetAccess: IInternetAccess): Observable<EntityResponseType> {
    return this.http.put<IInternetAccess>(this.resourceUrl, internetAccess, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IInternetAccess>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInternetAccess[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
