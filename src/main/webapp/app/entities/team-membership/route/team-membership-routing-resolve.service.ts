import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamMembership, TeamMembership } from '../team-membership.model';
import { TeamMembershipService } from '../service/team-membership.service';

@Injectable({ providedIn: 'root' })
export class TeamMembershipRoutingResolveService implements Resolve<ITeamMembership> {
  constructor(protected service: TeamMembershipService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITeamMembership> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((teamMembership: HttpResponse<TeamMembership>) => {
          if (teamMembership.body) {
            return of(teamMembership.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TeamMembership());
  }
}
