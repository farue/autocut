import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { BroadcastMessageComponent } from './list/broadcast-message.component';
import { BroadcastMessageDetailComponent } from './detail/broadcast-message-detail.component';
import { BroadcastMessageUpdateComponent } from './update/broadcast-message-update.component';
import { BroadcastMessageDeleteDialogComponent } from './delete/broadcast-message-delete-dialog.component';
import { BroadcastMessageRoutingModule } from './route/broadcast-message-routing.module';

@NgModule({
  imports: [SharedModule, BroadcastMessageRoutingModule],
  declarations: [
    BroadcastMessageComponent,
    BroadcastMessageDetailComponent,
    BroadcastMessageUpdateComponent,
    BroadcastMessageDeleteDialogComponent,
  ],
  entryComponents: [BroadcastMessageDeleteDialogComponent],
})
export class BroadcastMessageModule {}
