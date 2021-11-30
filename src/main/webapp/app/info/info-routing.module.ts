import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'application',
        loadChildren: () => import('./application/application.module').then(m => m.ApplicationModule),
      },
      {
        path: 'fee',
        loadChildren: () => import('./fee/fee.module').then(m => m.FeeModule),
      },
      {
        path: 'protocols',
        loadChildren: () => import('./protocols/protocols.module').then(m => m.ProtocolsModule),
      },
      {
        path: 'registration',
        loadChildren: () => import('./registration/registration.module').then(m => m.RegistrationModule),
      },
      {
        path: 'statutes',
        loadChildren: () => import('./statutes/statutes.module').then(m => m.StatutesModule),
      },
      {
        path: 'about',
        loadChildren: () => import('./about/about.module').then(m => m.AboutModule),
      },
    ]),
  ],
  // declarations: [ApplicationComponent, ContactsComponent, FeeComponent, ProtocolsComponent, RegistrationComponent]
})
export class InfoRoutingModule {}
