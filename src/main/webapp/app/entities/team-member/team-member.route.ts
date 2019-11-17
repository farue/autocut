import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ITeamMember, TeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';
import { TeamMemberComponent } from './team-member.component';
import { TeamMemberDetailComponent } from './team-member-detail.component';
import { TeamMemberUpdateComponent } from './team-member-update.component';
import { TeamMemberDeletePopupComponent } from './team-member-delete-dialog.component';

@Injectable({ providedIn: 'root' })
export class TeamMemberResolve implements Resolve<ITeamMember> {
  constructor(private service: TeamMemberService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITeamMember> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((teamMember: HttpResponse<TeamMember>) => teamMember.body));
    }
    return of(new TeamMember());
  }
}

export const teamMemberRoute: Routes = [
  {
    path: '',
    component: TeamMemberComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.teamMember.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: TeamMemberDetailComponent,
    resolve: {
      teamMember: TeamMemberResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.teamMember.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: TeamMemberUpdateComponent,
    resolve: {
      teamMember: TeamMemberResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.teamMember.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: TeamMemberUpdateComponent,
    resolve: {
      teamMember: TeamMemberResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.teamMember.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const teamMemberPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: TeamMemberDeletePopupComponent,
    resolve: {
      teamMember: TeamMemberResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'autocutApp.teamMember.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
