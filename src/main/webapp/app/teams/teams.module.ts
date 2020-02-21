import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';

import { TEAMS_ROUTE, TeamsComponent } from './';
import { NetworkTeamComponent } from './network-team/network-team.component';
import { WashingTeamComponent } from './washing-team/washing-team.component';
import { ToolsTeamComponent } from './tools-team/tools-team.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forRoot([TEAMS_ROUTE], { useHash: true })],
  declarations: [TeamsComponent, NetworkTeamComponent, WashingTeamComponent, ToolsTeamComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AutocutAppTeamsModule {}
