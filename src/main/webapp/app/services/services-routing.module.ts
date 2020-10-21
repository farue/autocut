import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'washing',
        loadChildren: () => import('./washing/washing.module').then(m => m.WashingModule),
      },
      {
        path: 'transactions',
        loadChildren: () => import('./transaction/transaction.module').then(m => m.TransactionModule),
      },
      {
        path: 'internet',
        loadChildren: () => import('./internet/internet.module').then(m => m.InternetModule),
      },
    ]),
  ],
})
export class ServicesRoutingModule {}
