import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getLeaseIdentifier, ILease } from '../lease.model';

export type EntityResponseType = HttpResponse<ILease>;
export type EntityArrayResponseType = HttpResponse<ILease[]>;

@Injectable({ providedIn: 'root' })
export class LeaseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/leases');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(lease: ILease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lease);
    return this.http
      .post<ILease>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(lease: ILease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lease);
    return this.http
      .put<ILease>(`${this.resourceUrl}/${getLeaseIdentifier(lease) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(lease: ILease): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lease);
    return this.http
      .patch<ILease>(`${this.resourceUrl}/${getLeaseIdentifier(lease) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILease>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILease[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLeaseToCollectionIfMissing(leaseCollection: ILease[], ...leasesToCheck: (ILease | null | undefined)[]): ILease[] {
    const leases: ILease[] = leasesToCheck.filter(isPresent);
    if (leases.length > 0) {
      const leaseCollectionIdentifiers = leaseCollection.map(leaseItem => getLeaseIdentifier(leaseItem)!);
      const leasesToAdd = leases.filter(leaseItem => {
        const leaseIdentifier = getLeaseIdentifier(leaseItem);
        if (leaseIdentifier == null || leaseCollectionIdentifiers.includes(leaseIdentifier)) {
          return false;
        }
        leaseCollectionIdentifiers.push(leaseIdentifier);
        return true;
      });
      return [...leasesToAdd, ...leaseCollection];
    }
    return leaseCollection;
  }

  protected convertDateFromClient(lease: ILease): ILease {
    return Object.assign({}, lease, {
      start: lease.start?.isValid() ? lease.start.format(DATE_FORMAT) : undefined,
      end: lease.end?.isValid() ? lease.end.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.start = res.body.start ? dayjs(res.body.start) : undefined;
      res.body.end = res.body.end ? dayjs(res.body.end) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((lease: ILease) => {
        lease.start = lease.start ? dayjs(lease.start) : undefined;
        lease.end = lease.end ? dayjs(lease.end) : undefined;
      });
    }
    return res;
  }
}
