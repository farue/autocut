import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { getActivityIdentifier, IActivity } from '../activity.model';

export type EntityResponseType = HttpResponse<IActivity>;
export type EntityArrayResponseType = HttpResponse<IActivity[]>;

@Injectable({ providedIn: 'root' })
export class ActivityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/activities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(activity: IActivity): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(activity);
    return this.http
      .post<IActivity>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(activity: IActivity): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(activity);
    return this.http
      .put<IActivity>(`${this.resourceUrl}/${getActivityIdentifier(activity) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(activity: IActivity): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(activity);
    return this.http
      .patch<IActivity>(`${this.resourceUrl}/${getActivityIdentifier(activity) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IActivity>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IActivity[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addActivityToCollectionIfMissing(activityCollection: IActivity[], ...activitiesToCheck: (IActivity | null | undefined)[]): IActivity[] {
    const activities: IActivity[] = activitiesToCheck.filter(isPresent);
    if (activities.length > 0) {
      const activityCollectionIdentifiers = activityCollection.map(activityItem => getActivityIdentifier(activityItem)!);
      const activitiesToAdd = activities.filter(activityItem => {
        const activityIdentifier = getActivityIdentifier(activityItem);
        if (activityIdentifier == null || activityCollectionIdentifiers.includes(activityIdentifier)) {
          return false;
        }
        activityCollectionIdentifiers.push(activityIdentifier);
        return true;
      });
      return [...activitiesToAdd, ...activityCollection];
    }
    return activityCollection;
  }

  protected convertDateFromClient(activity: IActivity): IActivity {
    return Object.assign({}, activity, {
      start: activity.start?.isValid() ? activity.start.format(DATE_FORMAT) : undefined,
      end: activity.end?.isValid() ? activity.end.format(DATE_FORMAT) : undefined,
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
      res.body.forEach((activity: IActivity) => {
        activity.start = activity.start ? dayjs(activity.start) : undefined;
        activity.end = activity.end ? dayjs(activity.end) : undefined;
      });
    }
    return res;
  }
}
