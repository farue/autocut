import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'application',
        loadChildren: () => import('./application/application.module').then(m => m.ApplicationModule)
      },
      {
        path: 'contacts',
        loadChildren: () => import('./contacts/contacts.module').then(m => m.ContactsModule)
      },
      {
        path: 'fee',
        loadChildren: () => import('./fee/fee.module').then(m => m.FeeModule)
      },
      {
        path: 'protocols',
        loadChildren: () => import('./protocols/protocols.module').then(m => m.ProtocolsModule)
      },
      {
        path: 'registration',
        loadChildren: () => import('./registration/registration.module').then(m => m.RegistrationModule)
      },
      {
        path: 'statutes',
        loadChildren: () => import('./statutes/statutes.module').then(m => m.StatutesModule)
      }
    ])
  ]
  // declarations: [ApplicationComponent, ContactsComponent, FeeComponent, ProtocolsComponent, RegistrationComponent]
})
export class InfoRoutingModule {}
