import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { JanitorComponent } from 'app/self-administration/janitor/janitor.component';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      {
        path: '',
        component: JanitorComponent,
        pathMatch: 'full',
      },
    ]),
  ],
  declarations: [JanitorComponent],
})
export class JanitorModule {}
