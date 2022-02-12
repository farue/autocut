import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getCommunicationIdentifier, ICommunication } from '../communication.model';

export type EntityResponseType = HttpResponse<ICommunication>;
export type EntityArrayResponseType = HttpResponse<ICommunication[]>;

@Injectable({ providedIn: 'root' })
export class CommunicationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/communications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(communication: ICommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(communication);
    return this.http
      .post<ICommunication>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(communication: ICommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(communication);
    return this.http
      .put<ICommunication>(`${this.resourceUrl}/${getCommunicationIdentifier(communication) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(communication: ICommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(communication);
    return this.http
      .patch<ICommunication>(`${this.resourceUrl}/${getCommunicationIdentifier(communication) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICommunication>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICommunication[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCommunicationToCollectionIfMissing(
    communicationCollection: ICommunication[],
    ...communicationsToCheck: (ICommunication | null | undefined)[]
  ): ICommunication[] {
    const communications: ICommunication[] = communicationsToCheck.filter(isPresent);
    if (communications.length > 0) {
      const communicationCollectionIdentifiers = communicationCollection.map(
        communicationItem => getCommunicationIdentifier(communicationItem)!
      );
      const communicationsToAdd = communications.filter(communicationItem => {
        const communicationIdentifier = getCommunicationIdentifier(communicationItem);
        if (communicationIdentifier == null || communicationCollectionIdentifiers.includes(communicationIdentifier)) {
          return false;
        }
        communicationCollectionIdentifiers.push(communicationIdentifier);
        return true;
      });
      return [...communicationsToAdd, ...communicationCollection];
    }
    return communicationCollection;
  }

  protected convertDateFromClient(communication: ICommunication): ICommunication {
    return Object.assign({}, communication, {
      date: communication.date?.isValid() ? communication.date.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((communication: ICommunication) => {
        communication.date = communication.date ? dayjs(communication.date) : undefined;
      });
    }
    return res;
  }
}
