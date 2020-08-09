import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TeamMemberComponent } from './team-member.component';
import { TeamMemberDetailComponent } from './team-member-detail.component';
import { TeamMemberUpdateComponent } from './team-member-update.component';
import { TeamMemberDeleteDialogComponent } from './team-member-delete-dialog.component';
import { teamMemberRoute } from './team-member.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(teamMemberRoute)],
  declarations: [TeamMemberComponent, TeamMemberDetailComponent, TeamMemberUpdateComponent, TeamMemberDeleteDialogComponent],
  entryComponents: [TeamMemberDeleteDialogComponent],
})
export class AutocutTeamMemberModule {}
