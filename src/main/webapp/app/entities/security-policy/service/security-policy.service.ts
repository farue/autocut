import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISecurityPolicy, getSecurityPolicyIdentifier } from '../security-policy.model';

export type EntityResponseType = HttpResponse<ISecurityPolicy>;
export type EntityArrayResponseType = HttpResponse<ISecurityPolicy[]>;

@Injectable({ providedIn: 'root' })
export class SecurityPolicyService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/security-policies');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(securityPolicy: ISecurityPolicy): Observable<EntityResponseType> {
    return this.http.post<ISecurityPolicy>(this.resourceUrl, securityPolicy, { observe: 'response' });
  }

  update(securityPolicy: ISecurityPolicy): Observable<EntityResponseType> {
    return this.http.put<ISecurityPolicy>(`${this.resourceUrl}/${getSecurityPolicyIdentifier(securityPolicy) as number}`, securityPolicy, {
      observe: 'response',
    });
  }

  partialUpdate(securityPolicy: ISecurityPolicy): Observable<EntityResponseType> {
    return this.http.patch<ISecurityPolicy>(
      `${this.resourceUrl}/${getSecurityPolicyIdentifier(securityPolicy) as number}`,
      securityPolicy,
      { observe: 'response' }
    );
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

  addSecurityPolicyToCollectionIfMissing(
    securityPolicyCollection: ISecurityPolicy[],
    ...securityPoliciesToCheck: (ISecurityPolicy | null | undefined)[]
  ): ISecurityPolicy[] {
    const securityPolicies: ISecurityPolicy[] = securityPoliciesToCheck.filter(isPresent);
    if (securityPolicies.length > 0) {
      const securityPolicyCollectionIdentifiers = securityPolicyCollection.map(
        securityPolicyItem => getSecurityPolicyIdentifier(securityPolicyItem)!
      );
      const securityPoliciesToAdd = securityPolicies.filter(securityPolicyItem => {
        const securityPolicyIdentifier = getSecurityPolicyIdentifier(securityPolicyItem);
        if (securityPolicyIdentifier == null || securityPolicyCollectionIdentifiers.includes(securityPolicyIdentifier)) {
          return false;
        }
        securityPolicyCollectionIdentifiers.push(securityPolicyIdentifier);
        return true;
      });
      return [...securityPoliciesToAdd, ...securityPolicyCollection];
    }
    return securityPolicyCollection;
  }
}
