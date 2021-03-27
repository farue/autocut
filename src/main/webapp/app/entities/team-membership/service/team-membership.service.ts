import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeamMembership, getTeamMembershipIdentifier } from '../team-membership.model';

export type EntityResponseType = HttpResponse<ITeamMembership>;
export type EntityArrayResponseType = HttpResponse<ITeamMembership[]>;

@Injectable({ providedIn: 'root' })
export class TeamMembershipService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/team-memberships');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(teamMembership: ITeamMembership): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teamMembership);
    return this.http
      .post<ITeamMembership>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(teamMembership: ITeamMembership): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teamMembership);
    return this.http
      .put<ITeamMembership>(`${this.resourceUrl}/${getTeamMembershipIdentifier(teamMembership) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(teamMembership: ITeamMembership): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teamMembership);
    return this.http
      .patch<ITeamMembership>(`${this.resourceUrl}/${getTeamMembershipIdentifier(teamMembership) as number}`, copy, { observe: 'response' })
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

  addTeamMembershipToCollectionIfMissing(
    teamMembershipCollection: ITeamMembership[],
    ...teamMembershipsToCheck: (ITeamMembership | null | undefined)[]
  ): ITeamMembership[] {
    const teamMemberships: ITeamMembership[] = teamMembershipsToCheck.filter(isPresent);
    if (teamMemberships.length > 0) {
      const teamMembershipCollectionIdentifiers = teamMembershipCollection.map(
        teamMembershipItem => getTeamMembershipIdentifier(teamMembershipItem)!
      );
      const teamMembershipsToAdd = teamMemberships.filter(teamMembershipItem => {
        const teamMembershipIdentifier = getTeamMembershipIdentifier(teamMembershipItem);
        if (teamMembershipIdentifier == null || teamMembershipCollectionIdentifiers.includes(teamMembershipIdentifier)) {
          return false;
        }
        teamMembershipCollectionIdentifiers.push(teamMembershipIdentifier);
        return true;
      });
      return [...teamMembershipsToAdd, ...teamMembershipCollection];
    }
    return teamMembershipCollection;
  }

  protected convertDateFromClient(teamMembership: ITeamMembership): ITeamMembership {
    return Object.assign({}, teamMembership, {
      start: teamMembership.start?.isValid() ? teamMembership.start.format(DATE_FORMAT) : undefined,
      end: teamMembership.end?.isValid() ? teamMembership.end.format(DATE_FORMAT) : undefined,
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
      res.body.forEach((teamMembership: ITeamMembership) => {
        teamMembership.start = teamMembership.start ? dayjs(teamMembership.start) : undefined;
        teamMembership.end = teamMembership.end ? dayjs(teamMembership.end) : undefined;
      });
    }
    return res;
  }
}
