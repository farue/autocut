import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ITeamMembership, TeamMembership } from 'app/shared/model/team-membership.model';
import { TeamMembershipService } from './team-membership.service';
import { TeamMembershipComponent } from './team-membership.component';
import { TeamMembershipDetailComponent } from './team-membership-detail.component';
import { TeamMembershipUpdateComponent } from './team-membership-update.component';

@Injectable({ providedIn: 'root' })
export class TeamMembershipResolve implements Resolve<ITeamMembership> {
  constructor(private service: TeamMembershipService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITeamMembership> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((teamMembership: HttpResponse<TeamMembership>) => {
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

export const teamMembershipRoute: Routes = [
  {
    path: '',
    component: TeamMembershipComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMembership.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamMembershipDetailComponent,
    resolve: {
      teamMembership: TeamMembershipResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMembership.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamMembershipUpdateComponent,
    resolve: {
      teamMembership: TeamMembershipResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMembership.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamMembershipUpdateComponent,
    resolve: {
      teamMembership: TeamMembershipResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'autocutApp.teamMembership.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
