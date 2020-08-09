import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IGlobalSetting } from 'app/shared/model/global-setting.model';

type EntityResponseType = HttpResponse<IGlobalSetting>;
type EntityArrayResponseType = HttpResponse<IGlobalSetting[]>;

@Injectable({ providedIn: 'root' })
export class GlobalSettingService {
  public resourceUrl = SERVER_API_URL + 'api/global-settings';

  constructor(protected http: HttpClient) {}

  create(globalSetting: IGlobalSetting): Observable<EntityResponseType> {
    return this.http.post<IGlobalSetting>(this.resourceUrl, globalSetting, { observe: 'response' });
  }

  update(globalSetting: IGlobalSetting): Observable<EntityResponseType> {
    return this.http.put<IGlobalSetting>(this.resourceUrl, globalSetting, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGlobalSetting>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGlobalSetting[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
