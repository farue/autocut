import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITeamMembership } from 'app/shared/model/team-membership.model';

type EntityResponseType = HttpResponse<ITeamMembership>;
type EntityArrayResponseType = HttpResponse<ITeamMembership[]>;

@Injectable({ providedIn: 'root' })
export class TeamMembershipService {
  public resourceUrl = SERVER_API_URL + 'api/team-memberships';

  constructor(protected http: HttpClient) {}

  create(teamMembership: ITeamMembership): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teamMembership);
    return this.http
      .post<ITeamMembership>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(teamMembership: ITeamMembership): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teamMembership);
    return this.http
      .put<ITeamMembership>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITeamMembership>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITeamMembership[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(teamMembership: ITeamMembership): ITeamMembership {
    const copy: ITeamMembership = Object.assign({}, teamMembership, {
      start: teamMembership.start && teamMembership.start.isValid() ? teamMembership.start.format(DATE_FORMAT) : undefined,
      end: teamMembership.end && teamMembership.end.isValid() ? teamMembership.end.format(DATE_FORMAT) : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.start = res.body.start ? moment(res.body.start) : undefined;
      res.body.end = res.body.end ? moment(res.body.end) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((teamMembership: ITeamMembership) => {
        teamMembership.start = teamMembership.start ? moment(teamMembership.start) : undefined;
        teamMembership.end = teamMembership.end ? moment(teamMembership.end) : undefined;
      });
    }
    return res;
  }
}
