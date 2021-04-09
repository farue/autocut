import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SelfAdministrationComponent } from 'app/self-administration/self-administration.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: SelfAdministrationComponent,
        pathMatch: 'full',
      },
      {
        path: 'spokesman',
        loadChildren: () => import('./spokesman/spokesman.module').then(m => m.SpokesmanModule),
      },
      {
        path: 'assignment-committee',
        loadChildren: () => import('./assignment-committee/assignment-committee.module').then(m => m.AssignmentCommitteeModule),
      },
      {
        path: 'janitor',
        loadChildren: () => import('./janitor/janitor.module').then(m => m.JanitorModule),
      },
    ]),
  ],
  exports: [RouterModule],
})
export class SelfAdministrationRoutingModule {}
