import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { INetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';

type EntityResponseType = HttpResponse<INetworkSwitchStatus>;
type EntityArrayResponseType = HttpResponse<INetworkSwitchStatus[]>;

@Injectable({ providedIn: 'root' })
export class NetworkSwitchStatusService {
  public resourceUrl = SERVER_API_URL + 'api/network-switch-statuses';

  constructor(protected http: HttpClient) {}

  create(networkSwitchStatus: INetworkSwitchStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(networkSwitchStatus);
    return this.http
      .post<INetworkSwitchStatus>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(networkSwitchStatus: INetworkSwitchStatus): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(networkSwitchStatus);
    return this.http
      .put<INetworkSwitchStatus>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INetworkSwitchStatus>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INetworkSwitchStatus[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(networkSwitchStatus: INetworkSwitchStatus): INetworkSwitchStatus {
    const copy: INetworkSwitchStatus = Object.assign({}, networkSwitchStatus, {
      timestamp:
        networkSwitchStatus.timestamp && networkSwitchStatus.timestamp.isValid() ? networkSwitchStatus.timestamp.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.timestamp = res.body.timestamp ? moment(res.body.timestamp) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((networkSwitchStatus: INetworkSwitchStatus) => {
        networkSwitchStatus.timestamp = networkSwitchStatus.timestamp ? moment(networkSwitchStatus.timestamp) : undefined;
      });
    }
    return res;
  }
}
