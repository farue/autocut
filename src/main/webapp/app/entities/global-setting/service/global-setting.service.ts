import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGlobalSetting, getGlobalSettingIdentifier } from '../global-setting.model';

export type EntityResponseType = HttpResponse<IGlobalSetting>;
export type EntityArrayResponseType = HttpResponse<IGlobalSetting[]>;

@Injectable({ providedIn: 'root' })
export class GlobalSettingService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/global-settings');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(globalSetting: IGlobalSetting): Observable<EntityResponseType> {
    return this.http.post<IGlobalSetting>(this.resourceUrl, globalSetting, { observe: 'response' });
  }

  update(globalSetting: IGlobalSetting): Observable<EntityResponseType> {
    return this.http.put<IGlobalSetting>(`${this.resourceUrl}/${getGlobalSettingIdentifier(globalSetting) as number}`, globalSetting, {
      observe: 'response',
    });
  }

  partialUpdate(globalSetting: IGlobalSetting): Observable<EntityResponseType> {
    return this.http.patch<IGlobalSetting>(`${this.resourceUrl}/${getGlobalSettingIdentifier(globalSetting) as number}`, globalSetting, {
      observe: 'response',
    });
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

  addGlobalSettingToCollectionIfMissing(
    globalSettingCollection: IGlobalSetting[],
    ...globalSettingsToCheck: (IGlobalSetting | null | undefined)[]
  ): IGlobalSetting[] {
    const globalSettings: IGlobalSetting[] = globalSettingsToCheck.filter(isPresent);
    if (globalSettings.length > 0) {
      const globalSettingCollectionIdentifiers = globalSettingCollection.map(
        globalSettingItem => getGlobalSettingIdentifier(globalSettingItem)!
      );
      const globalSettingsToAdd = globalSettings.filter(globalSettingItem => {
        const globalSettingIdentifier = getGlobalSettingIdentifier(globalSettingItem);
        if (globalSettingIdentifier == null || globalSettingCollectionIdentifiers.includes(globalSettingIdentifier)) {
          return false;
        }
        globalSettingCollectionIdentifiers.push(globalSettingIdentifier);
        return true;
      });
      return [...globalSettingsToAdd, ...globalSettingCollection];
    }
    return globalSettingCollection;
  }
}
