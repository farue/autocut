import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ToolsTeamComponent } from 'app/teams/tools-team/tools-team.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: ToolsTeamComponent,
        pathMatch: 'full',
      },
    ]),
  ],
  declarations: [ToolsTeamComponent],
})
export class ToolsTeamModule {}
