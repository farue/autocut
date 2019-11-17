import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TeamMemberComponent } from './team-member.component';
import { TeamMemberDetailComponent } from './team-member-detail.component';
import { TeamMemberUpdateComponent } from './team-member-update.component';
import { TeamMemberDeleteDialogComponent, TeamMemberDeletePopupComponent } from './team-member-delete-dialog.component';
import { teamMemberPopupRoute, teamMemberRoute } from './team-member.route';

const ENTITY_STATES = [...teamMemberRoute, ...teamMemberPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    TeamMemberComponent,
    TeamMemberDetailComponent,
    TeamMemberUpdateComponent,
    TeamMemberDeleteDialogComponent,
    TeamMemberDeletePopupComponent
  ],
  entryComponents: [TeamMemberDeleteDialogComponent]
})
export class AutocutTeamMemberModule {}
