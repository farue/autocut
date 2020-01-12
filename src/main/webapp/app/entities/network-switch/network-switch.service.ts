import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { INetworkSwitch } from 'app/shared/model/network-switch.model';

type EntityResponseType = HttpResponse<INetworkSwitch>;
type EntityArrayResponseType = HttpResponse<INetworkSwitch[]>;

@Injectable({ providedIn: 'root' })
export class NetworkSwitchService {
  public resourceUrl = SERVER_API_URL + 'api/network-switches';

  constructor(protected http: HttpClient) {}

  create(networkSwitch: INetworkSwitch): Observable<EntityResponseType> {
    return this.http.post<INetworkSwitch>(this.resourceUrl, networkSwitch, { observe: 'response' });
  }

  update(networkSwitch: INetworkSwitch): Observable<EntityResponseType> {
    return this.http.put<INetworkSwitch>(this.resourceUrl, networkSwitch, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INetworkSwitch>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INetworkSwitch[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
