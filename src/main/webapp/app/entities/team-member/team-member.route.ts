import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ITeamMember, TeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';
import { TeamMemberComponent } from './team-member.component';
import { TeamMemberDetailComponent } from './team-member-detail.component';
import { TeamMemberUpdateComponent } from './team-member-update.component';

@Injectable({ providedIn: 'root' })
export class TeamMemberResolve implements Resolve<ITeamMember> {
  constructor(private service: TeamMemberService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITeamMember> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((teamMember: HttpResponse<TeamMember>) => {
          if (teamMember.body) {
            return of(teamMember.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TeamMember());
  }
}

export const teamMemberRoute: Routes = [
  {
    path: '',
    component: TeamMemberComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMember.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamMemberDetailComponent,
    resolve: {
      teamMember: TeamMemberResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMember.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamMemberUpdateComponent,
    resolve: {
      teamMember: TeamMemberResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMember.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamMemberUpdateComponent,
    resolve: {
      teamMember: TeamMemberResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMember.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
