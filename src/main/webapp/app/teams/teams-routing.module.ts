import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";

@NgModule({
  imports: [RouterModule.forChild([
    {
      path: 'networking',
      loadChildren: () => import('./networking-team/networking-team.module').then(m => m.NetworkingTeamModule)
    },
    {
      path: 'washing',
      loadChildren: () => import('./washing-team/washing-team.module').then(m => m.WashingTeamModule)
    },
    {
      path: 'tools',
      loadChildren: () => import('./tools-team/tools-team.module').then(m => m.ToolsTeamModule)
    }
  ])],
  exports: [RouterModule],
})
export class TeamsRoutingModule {}
