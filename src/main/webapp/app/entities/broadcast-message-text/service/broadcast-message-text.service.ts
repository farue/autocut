import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getBroadcastMessageTextIdentifier, IBroadcastMessageText } from '../broadcast-message-text.model';

export type EntityResponseType = HttpResponse<IBroadcastMessageText>;
export type EntityArrayResponseType = HttpResponse<IBroadcastMessageText[]>;

@Injectable({ providedIn: 'root' })
export class BroadcastMessageTextService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/broadcast-message-texts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(broadcastMessageText: IBroadcastMessageText): Observable<EntityResponseType> {
    return this.http.post<IBroadcastMessageText>(this.resourceUrl, broadcastMessageText, { observe: 'response' });
  }

  update(broadcastMessageText: IBroadcastMessageText): Observable<EntityResponseType> {
    return this.http.put<IBroadcastMessageText>(
      `${this.resourceUrl}/${getBroadcastMessageTextIdentifier(broadcastMessageText) as number}`,
      broadcastMessageText,
      { observe: 'response' }
    );
  }

  partialUpdate(broadcastMessageText: IBroadcastMessageText): Observable<EntityResponseType> {
    return this.http.patch<IBroadcastMessageText>(
      `${this.resourceUrl}/${getBroadcastMessageTextIdentifier(broadcastMessageText) as number}`,
      broadcastMessageText,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBroadcastMessageText>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBroadcastMessageText[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBroadcastMessageTextToCollectionIfMissing(
    broadcastMessageTextCollection: IBroadcastMessageText[],
    ...broadcastMessageTextsToCheck: (IBroadcastMessageText | null | undefined)[]
  ): IBroadcastMessageText[] {
    const broadcastMessageTexts: IBroadcastMessageText[] = broadcastMessageTextsToCheck.filter(isPresent);
    if (broadcastMessageTexts.length > 0) {
      const broadcastMessageTextCollectionIdentifiers = broadcastMessageTextCollection.map(
        broadcastMessageTextItem => getBroadcastMessageTextIdentifier(broadcastMessageTextItem)!
      );
      const broadcastMessageTextsToAdd = broadcastMessageTexts.filter(broadcastMessageTextItem => {
        const broadcastMessageTextIdentifier = getBroadcastMessageTextIdentifier(broadcastMessageTextItem);
        if (broadcastMessageTextIdentifier == null || broadcastMessageTextCollectionIdentifiers.includes(broadcastMessageTextIdentifier)) {
          return false;
        }
        broadcastMessageTextCollectionIdentifiers.push(broadcastMessageTextIdentifier);
        return true;
      });
      return [...broadcastMessageTextsToAdd, ...broadcastMessageTextCollection];
    }
    return broadcastMessageTextCollection;
  }
}
