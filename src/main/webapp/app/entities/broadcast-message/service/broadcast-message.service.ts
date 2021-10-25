import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getBroadcastMessageIdentifier, IBroadcastMessage } from '../broadcast-message.model';

export type EntityResponseType = HttpResponse<IBroadcastMessage>;
export type EntityArrayResponseType = HttpResponse<IBroadcastMessage[]>;

@Injectable({ providedIn: 'root' })
export class BroadcastMessageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/broadcast-messages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(broadcastMessage: IBroadcastMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(broadcastMessage);
    return this.http
      .post<IBroadcastMessage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(broadcastMessage: IBroadcastMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(broadcastMessage);
    return this.http
      .put<IBroadcastMessage>(`${this.resourceUrl}/${getBroadcastMessageIdentifier(broadcastMessage) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(broadcastMessage: IBroadcastMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(broadcastMessage);
    return this.http
      .patch<IBroadcastMessage>(`${this.resourceUrl}/${getBroadcastMessageIdentifier(broadcastMessage) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBroadcastMessage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBroadcastMessage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBroadcastMessageToCollectionIfMissing(
    broadcastMessageCollection: IBroadcastMessage[],
    ...broadcastMessagesToCheck: (IBroadcastMessage | null | undefined)[]
  ): IBroadcastMessage[] {
    const broadcastMessages: IBroadcastMessage[] = broadcastMessagesToCheck.filter(isPresent);
    if (broadcastMessages.length > 0) {
      const broadcastMessageCollectionIdentifiers = broadcastMessageCollection.map(
        broadcastMessageItem => getBroadcastMessageIdentifier(broadcastMessageItem)!
      );
      const broadcastMessagesToAdd = broadcastMessages.filter(broadcastMessageItem => {
        const broadcastMessageIdentifier = getBroadcastMessageIdentifier(broadcastMessageItem);
        if (broadcastMessageIdentifier == null || broadcastMessageCollectionIdentifiers.includes(broadcastMessageIdentifier)) {
          return false;
        }
        broadcastMessageCollectionIdentifiers.push(broadcastMessageIdentifier);
        return true;
      });
      return [...broadcastMessagesToAdd, ...broadcastMessageCollection];
    }
    return broadcastMessageCollection;
  }

  protected convertDateFromClient(broadcastMessage: IBroadcastMessage): IBroadcastMessage {
    return Object.assign({}, broadcastMessage, {
      start: broadcastMessage.start?.isValid() ? broadcastMessage.start.toJSON() : undefined,
      end: broadcastMessage.end?.isValid() ? broadcastMessage.end.toJSON() : undefined,
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
      res.body.forEach((broadcastMessage: IBroadcastMessage) => {
        broadcastMessage.start = broadcastMessage.start ? dayjs(broadcastMessage.start) : undefined;
        broadcastMessage.end = broadcastMessage.end ? dayjs(broadcastMessage.end) : undefined;
      });
    }
    return res;
  }
}
