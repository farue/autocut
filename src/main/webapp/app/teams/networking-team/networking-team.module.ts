import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NetworkingTeamComponent } from 'app/teams/networking-team/networking-team.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: NetworkingTeamComponent,
        pathMatch: 'full',
      },
    ]),
  ],
  declarations: [NetworkingTeamComponent],
})
export class NetworkingTeamModule {}
