import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tenant',
        loadChildren: () => import('./tenant/tenant.module').then(m => m.AutocutTenantModule),
      },
      {
        path: 'team',
        loadChildren: () => import('./team/team.module').then(m => m.AutocutTeamModule),
      },
      {
        path: 'lease',
        loadChildren: () => import('./lease/lease.module').then(m => m.AutocutLeaseModule),
      },
      {
        path: 'apartment',
        loadChildren: () => import('./apartment/apartment.module').then(m => m.AutocutApartmentModule),
      },
      {
        path: 'address',
        loadChildren: () => import('./address/address.module').then(m => m.AutocutAddressModule),
      },
      {
        path: 'security-policy',
        loadChildren: () => import('./security-policy/security-policy.module').then(m => m.AutocutSecurityPolicyModule),
      },
      {
        path: 'internet-access',
        loadChildren: () => import('./internet-access/internet-access.module').then(m => m.AutocutInternetAccessModule),
      },
      {
        path: 'tenant-communication',
        loadChildren: () => import('./tenant-communication/tenant-communication.module').then(m => m.AutocutTenantCommunicationModule),
      },
      {
        path: 'activity',
        loadChildren: () => import('./activity/activity.module').then(m => m.AutocutActivityModule),
      },
      {
        path: 'communication',
        loadChildren: () => import('./communication/communication.module').then(m => m.AutocutCommunicationModule),
      },
      {
        path: 'laundry-machine',
        loadChildren: () => import('./laundry-machine/laundry-machine.module').then(m => m.AutocutLaundryMachineModule),
      },
      {
        path: 'laundry-machine-program',
        loadChildren: () =>
          import('./laundry-machine-program/laundry-machine-program.module').then(m => m.AutocutLaundryMachineProgramModule),
      },
      {
        path: 'wash-history',
        loadChildren: () => import('./wash-history/wash-history.module').then(m => m.AutocutWashHistoryModule),
      },
      {
        path: 'global-setting',
        loadChildren: () => import('./global-setting/global-setting.module').then(m => m.AutocutGlobalSettingModule),
      },
      {
        path: 'network-switch',
        loadChildren: () => import('./network-switch/network-switch.module').then(m => m.AutocutNetworkSwitchModule),
      },
      {
        path: 'transaction-book',
        loadChildren: () => import('./transaction-book/transaction-book.module').then(m => m.AutocutTransactionBookModule),
      },
      {
        path: 'team-membership',
        loadChildren: () => import('./team-membership/team-membership.module').then(m => m.AutocutTeamMembershipModule),
      },
      {
        path: 'bank-account',
        loadChildren: () => import('./bank-account/bank-account.module').then(m => m.AutocutBankAccountModule),
      },
      {
        path: 'bank-transaction',
        loadChildren: () => import('./bank-transaction/bank-transaction.module').then(m => m.AutocutBankTransactionModule),
      },
      {
        path: 'internal-transaction',
        loadChildren: () => import('./internal-transaction/internal-transaction.module').then(m => m.AutocutInternalTransactionModule),
      },
      {
        path: 'network-switch-status',
        loadChildren: () => import('./network-switch-status/network-switch-status.module').then(m => m.AutocutNetworkSwitchStatusModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class AutocutEntityModule {}
