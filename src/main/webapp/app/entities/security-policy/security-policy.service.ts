import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISecurityPolicy } from 'app/shared/model/security-policy.model';

type EntityResponseType = HttpResponse<ISecurityPolicy>;
type EntityArrayResponseType = HttpResponse<ISecurityPolicy[]>;

@Injectable({ providedIn: 'root' })
export class SecurityPolicyService {
  public resourceUrl = SERVER_API_URL + 'api/security-policies';

  constructor(protected http: HttpClient) {}

  create(securityPolicy: ISecurityPolicy): Observable<EntityResponseType> {
    return this.http.post<ISecurityPolicy>(this.resourceUrl, securityPolicy, { observe: 'response' });
  }

  update(securityPolicy: ISecurityPolicy): Observable<EntityResponseType> {
    return this.http.put<ISecurityPolicy>(this.resourceUrl, securityPolicy, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISecurityPolicy>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISecurityPolicy[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
