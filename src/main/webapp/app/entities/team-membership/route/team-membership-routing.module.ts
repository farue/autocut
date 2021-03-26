import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TeamMembershipComponent } from '../list/team-membership.component';
import { TeamMembershipDetailComponent } from '../detail/team-membership-detail.component';
import { TeamMembershipUpdateComponent } from '../update/team-membership-update.component';
import { TeamMembershipRoutingResolveService } from './team-membership-routing-resolve.service';

const teamMembershipRoute: Routes = [
  {
    path: '',
    component: TeamMembershipComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamMembershipDetailComponent,
    resolve: {
      teamMembership: TeamMembershipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamMembershipUpdateComponent,
    resolve: {
      teamMembership: TeamMembershipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamMembershipUpdateComponent,
    resolve: {
      teamMembership: TeamMembershipRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(teamMembershipRoute)],
  exports: [RouterModule],
})
export class TeamMembershipRoutingModule {}
