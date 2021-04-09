import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WashingTeamComponent } from 'app/teams/washing-team/washing-team.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: WashingTeamComponent,
        pathMatch: 'full',
      },
    ]),
  ],
  declarations: [WashingTeamComponent],
})
export class WashingTeamModule {}
