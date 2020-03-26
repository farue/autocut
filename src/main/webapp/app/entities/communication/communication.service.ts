import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICommunication } from 'app/shared/model/communication.model';

type EntityResponseType = HttpResponse<ICommunication>;
type EntityArrayResponseType = HttpResponse<ICommunication[]>;

@Injectable({ providedIn: 'root' })
export class CommunicationService {
  public resourceUrl = SERVER_API_URL + 'api/communications';

  constructor(protected http: HttpClient) {}

  create(communication: ICommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(communication);
    return this.http
      .post<ICommunication>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(communication: ICommunication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(communication);
    return this.http
      .put<ICommunication>(this.resourceUrl, copy, { observe: 'response' })
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

  protected convertDateFromClient(communication: ICommunication): ICommunication {
    const copy: ICommunication = Object.assign({}, communication, {
      date: communication.date && communication.date.isValid() ? communication.date.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((communication: ICommunication) => {
        communication.date = communication.date ? moment(communication.date) : undefined;
      });
    }
    return res;
  }
}
