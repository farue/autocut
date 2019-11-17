import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TeamComponent } from './team.component';
import { TeamDetailComponent } from './team-detail.component';
import { TeamUpdateComponent } from './team-update.component';
import { TeamDeleteDialogComponent, TeamDeletePopupComponent } from './team-delete-dialog.component';
import { teamPopupRoute, teamRoute } from './team.route';

const ENTITY_STATES = [...teamRoute, ...teamPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [TeamComponent, TeamDetailComponent, TeamUpdateComponent, TeamDeleteDialogComponent, TeamDeletePopupComponent],
  entryComponents: [TeamDeleteDialogComponent]
})
export class AutocutTeamModule {}
