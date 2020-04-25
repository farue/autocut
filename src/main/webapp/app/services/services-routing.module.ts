import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'washing',
        loadChildren: () => import('./washing/washing.module').then(m => m.WashingModule)
      }
    ])
  ]
})
export class ServicesRoutingModule {}
