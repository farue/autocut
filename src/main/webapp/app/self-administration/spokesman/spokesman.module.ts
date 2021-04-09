import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { SpokesmanComponent } from 'app/self-administration/spokesman/spokesman.component';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: SpokesmanComponent,
        pathMatch: 'full',
      },
    ]),
  ],
  declarations: [SpokesmanComponent],
})
export class SpokesmanModule {}
