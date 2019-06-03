import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from '../shared';

import { TEAMS_ROUTE, TeamsComponent } from './';
import { NetworkTeamComponent } from './network-team/network-team.component';
import { WashTeamComponent } from './wash-team/wash-team.component';
import { ToolsTeamComponent } from './tools-team/tools-team.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forRoot([TEAMS_ROUTE], { useHash: true })],
  declarations: [TeamsComponent, NetworkTeamComponent, WashTeamComponent, ToolsTeamComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AutocutAppTeamsModule {}
