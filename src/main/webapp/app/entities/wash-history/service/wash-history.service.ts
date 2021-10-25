import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getWashHistoryIdentifier, IWashHistory } from '../wash-history.model';

export type EntityResponseType = HttpResponse<IWashHistory>;
export type EntityArrayResponseType = HttpResponse<IWashHistory[]>;

@Injectable({ providedIn: 'root' })
export class WashHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/wash-histories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(washHistory: IWashHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(washHistory);
    return this.http
      .post<IWashHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(washHistory: IWashHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(washHistory);
    return this.http
      .put<IWashHistory>(`${this.resourceUrl}/${getWashHistoryIdentifier(washHistory) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(washHistory: IWashHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(washHistory);
    return this.http
      .patch<IWashHistory>(`${this.resourceUrl}/${getWashHistoryIdentifier(washHistory) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IWashHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IWashHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addWashHistoryToCollectionIfMissing(
    washHistoryCollection: IWashHistory[],
    ...washHistoriesToCheck: (IWashHistory | null | undefined)[]
  ): IWashHistory[] {
    const washHistories: IWashHistory[] = washHistoriesToCheck.filter(isPresent);
    if (washHistories.length > 0) {
      const washHistoryCollectionIdentifiers = washHistoryCollection.map(washHistoryItem => getWashHistoryIdentifier(washHistoryItem)!);
      const washHistoriesToAdd = washHistories.filter(washHistoryItem => {
        const washHistoryIdentifier = getWashHistoryIdentifier(washHistoryItem);
        if (washHistoryIdentifier == null || washHistoryCollectionIdentifiers.includes(washHistoryIdentifier)) {
          return false;
        }
        washHistoryCollectionIdentifiers.push(washHistoryIdentifier);
        return true;
      });
      return [...washHistoriesToAdd, ...washHistoryCollection];
    }
    return washHistoryCollection;
  }

  protected convertDateFromClient(washHistory: IWashHistory): IWashHistory {
    return Object.assign({}, washHistory, {
      usingDate: washHistory.usingDate?.isValid() ? washHistory.usingDate.toJSON() : undefined,
      reservationDate: washHistory.reservationDate?.isValid() ? washHistory.reservationDate.toJSON() : undefined,
      lastModifiedDate: washHistory.lastModifiedDate?.isValid() ? washHistory.lastModifiedDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.usingDate = res.body.usingDate ? dayjs(res.body.usingDate) : undefined;
      res.body.reservationDate = res.body.reservationDate ? dayjs(res.body.reservationDate) : undefined;
      res.body.lastModifiedDate = res.body.lastModifiedDate ? dayjs(res.body.lastModifiedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((washHistory: IWashHistory) => {
        washHistory.usingDate = washHistory.usingDate ? dayjs(washHistory.usingDate) : undefined;
        washHistory.reservationDate = washHistory.reservationDate ? dayjs(washHistory.reservationDate) : undefined;
        washHistory.lastModifiedDate = washHistory.lastModifiedDate ? dayjs(washHistory.lastModifiedDate) : undefined;
      });
    }
    return res;
  }
}
