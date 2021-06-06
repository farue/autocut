import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'washing',
        loadChildren: () => import('./washing/washing-routing.module').then(m => m.WashingRoutingModule),
      },
      {
        path: 'transactions',
        loadChildren: () => import('./transaction/transaction-routing.module').then(m => m.TransactionRoutingModule),
      },
      {
        path: 'internet',
        loadChildren: () => import('./internet/internet-routing.module').then(m => m.InternetRoutingModule),
      },
    ]),
  ],
})
export class ServicesRoutingModule {}
