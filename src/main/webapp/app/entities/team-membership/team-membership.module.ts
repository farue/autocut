import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TeamMembershipComponent } from './team-membership.component';
import { TeamMembershipDetailComponent } from './team-membership-detail.component';
import { TeamMembershipUpdateComponent } from './team-membership-update.component';
import { TeamMembershipDeleteDialogComponent } from './team-membership-delete-dialog.component';
import { teamMembershipRoute } from './team-membership.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(teamMembershipRoute)],
  declarations: [
    TeamMembershipComponent,
    TeamMembershipDetailComponent,
    TeamMembershipUpdateComponent,
    TeamMembershipDeleteDialogComponent,
  ],
  entryComponents: [TeamMembershipDeleteDialogComponent],
})
export class AutocutTeamMembershipModule {}
