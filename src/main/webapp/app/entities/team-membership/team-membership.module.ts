import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TeamMembershipComponent } from './list/team-membership.component';
import { TeamMembershipDetailComponent } from './detail/team-membership-detail.component';
import { TeamMembershipUpdateComponent } from './update/team-membership-update.component';
import { TeamMembershipDeleteDialogComponent } from './delete/team-membership-delete-dialog.component';
import { TeamMembershipRoutingModule } from './route/team-membership-routing.module';

@NgModule({
  imports: [SharedModule, TeamMembershipRoutingModule],
  declarations: [
    TeamMembershipComponent,
    TeamMembershipDetailComponent,
    TeamMembershipUpdateComponent,
    TeamMembershipDeleteDialogComponent,
  ],
  entryComponents: [TeamMembershipDeleteDialogComponent],
})
export class TeamMembershipModule {}
