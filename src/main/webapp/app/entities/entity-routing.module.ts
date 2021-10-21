import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tenant',
        data: { pageTitle: 'autocutApp.tenant.home.title' },
        loadChildren: () => import('./tenant/tenant.module').then(m => m.TenantModule),
      },
      {
        path: 'team',
        data: { pageTitle: 'autocutApp.team.home.title' },
        loadChildren: () => import('./team/team.module').then(m => m.TeamModule),
      },
      {
        path: 'lease',
        data: { pageTitle: 'autocutApp.lease.home.title' },
        loadChildren: () => import('./lease/lease.module').then(m => m.LeaseModule),
      },
      {
        path: 'apartment',
        data: { pageTitle: 'autocutApp.apartment.home.title' },
        loadChildren: () => import('./apartment/apartment.module').then(m => m.ApartmentModule),
      },
      {
        path: 'address',
        data: { pageTitle: 'autocutApp.address.home.title' },
        loadChildren: () => import('./address/address.module').then(m => m.AddressModule),
      },
      {
        path: 'security-policy',
        data: { pageTitle: 'autocutApp.securityPolicy.home.title' },
        loadChildren: () => import('./security-policy/security-policy.module').then(m => m.SecurityPolicyModule),
      },
      {
        path: 'internet-access',
        data: { pageTitle: 'autocutApp.internetAccess.home.title' },
        loadChildren: () => import('./internet-access/internet-access.module').then(m => m.InternetAccessModule),
      },
      {
        path: 'internal-transaction',
        data: { pageTitle: 'autocutApp.internalTransaction.home.title' },
        loadChildren: () => import('./internal-transaction/internal-transaction.module').then(m => m.InternalTransactionModule),
      },
      {
        path: 'tenant-communication',
        data: { pageTitle: 'autocutApp.tenantCommunication.home.title' },
        loadChildren: () => import('./tenant-communication/tenant-communication.module').then(m => m.TenantCommunicationModule),
      },
      {
        path: 'activity',
        data: { pageTitle: 'autocutApp.activity.home.title' },
        loadChildren: () => import('./activity/activity.module').then(m => m.ActivityModule),
      },
      {
        path: 'communication',
        data: { pageTitle: 'autocutApp.communication.home.title' },
        loadChildren: () => import('./communication/communication.module').then(m => m.CommunicationModule),
      },
      {
        path: 'laundry-machine',
        data: { pageTitle: 'autocutApp.laundryMachine.home.title' },
        loadChildren: () => import('./laundry-machine/laundry-machine.module').then(m => m.LaundryMachineModule),
      },
      {
        path: 'laundry-machine-program',
        data: { pageTitle: 'autocutApp.laundryMachineProgram.home.title' },
        loadChildren: () => import('./laundry-machine-program/laundry-machine-program.module').then(m => m.LaundryMachineProgramModule),
      },
      {
        path: 'wash-history',
        data: { pageTitle: 'autocutApp.washHistory.home.title' },
        loadChildren: () => import('./wash-history/wash-history.module').then(m => m.WashHistoryModule),
      },
      {
        path: 'global-setting',
        data: { pageTitle: 'autocutApp.globalSetting.home.title' },
        loadChildren: () => import('./global-setting/global-setting.module').then(m => m.GlobalSettingModule),
      },
      {
        path: 'network-switch',
        data: { pageTitle: 'autocutApp.networkSwitch.home.title' },
        loadChildren: () => import('./network-switch/network-switch.module').then(m => m.NetworkSwitchModule),
      },
      {
        path: 'transaction-book',
        data: { pageTitle: 'autocutApp.transactionBook.home.title' },
        loadChildren: () => import('./transaction-book/transaction-book.module').then(m => m.TransactionBookModule),
      },
      {
        path: 'team-membership',
        data: { pageTitle: 'autocutApp.teamMembership.home.title' },
        loadChildren: () => import('./team-membership/team-membership.module').then(m => m.TeamMembershipModule),
      },
      {
        path: 'bank-account',
        data: { pageTitle: 'autocutApp.bankAccount.home.title' },
        loadChildren: () => import('./bank-account/bank-account.module').then(m => m.BankAccountModule),
      },
      {
        path: 'bank-transaction',
        data: { pageTitle: 'autocutApp.bankTransaction.home.title' },
        loadChildren: () => import('./bank-transaction/bank-transaction.module').then(m => m.BankTransactionModule),
      },
      {
        path: 'network-switch-status',
        data: { pageTitle: 'autocutApp.networkSwitchStatus.home.title' },
        loadChildren: () => import('./network-switch-status/network-switch-status.module').then(m => m.NetworkSwitchStatusModule),
      },
      {
        path: 'laundry-program',
        data: { pageTitle: 'autocutApp.laundryProgram.home.title' },
        loadChildren: () => import('./laundry-program/laundry-program.module').then(m => m.LaundryProgramModule),
      },
      {
        path: 'broadcast-message',
        data: { pageTitle: 'autocutApp.broadcastMessage.home.title' },
        loadChildren: () => import('./broadcast-message/broadcast-message.module').then(m => m.BroadcastMessageModule),
      },
      {
        path: 'broadcast-message-text',
        data: { pageTitle: 'autocutApp.broadcastMessageText.home.title' },
        loadChildren: () => import('./broadcast-message-text/broadcast-message-text.module').then(m => m.BroadcastMessageTextModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
