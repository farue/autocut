import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TeamsRoutingModule } from "app/teams/teams-routing.module";

@NgModule({
  imports: [SharedModule, TeamsRoutingModule],
  declarations: [],
})
export class TeamsModule {}
