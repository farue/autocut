import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ITeamMember } from 'app/shared/model/team-member.model';

type EntityResponseType = HttpResponse<ITeamMember>;
type EntityArrayResponseType = HttpResponse<ITeamMember[]>;

@Injectable({ providedIn: 'root' })
export class TeamMemberService {
  public resourceUrl = SERVER_API_URL + 'api/team-members';

  constructor(protected http: HttpClient) {}

  create(teamMember: ITeamMember): Observable<EntityResponseType> {
    return this.http.post<ITeamMember>(this.resourceUrl, teamMember, { observe: 'response' });
  }

  update(teamMember: ITeamMember): Observable<EntityResponseType> {
    return this.http.put<ITeamMember>(this.resourceUrl, teamMember, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamMember>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamMember[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
