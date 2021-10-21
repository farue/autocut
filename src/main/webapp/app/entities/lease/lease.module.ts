import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {LeaseComponent} from './list/lease.component';
import {LeaseDetailComponent} from './detail/lease-detail.component';
import {LeaseUpdateComponent} from './update/lease-update.component';
import {LeaseDeleteDialogComponent} from './delete/lease-delete-dialog.component';
import {LeaseRoutingModule} from './route/lease-routing.module';

@NgModule({
  imports: [SharedModule, LeaseRoutingModule],
  declarations: [LeaseComponent, LeaseDetailComponent, LeaseUpdateComponent, LeaseDeleteDialogComponent],
  entryComponents: [LeaseDeleteDialogComponent],
})
export class LeaseModule {}
