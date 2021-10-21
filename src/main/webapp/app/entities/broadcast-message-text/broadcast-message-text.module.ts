import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {BroadcastMessageTextComponent} from './list/broadcast-message-text.component';
import {BroadcastMessageTextDetailComponent} from './detail/broadcast-message-text-detail.component';
import {BroadcastMessageTextUpdateComponent} from './update/broadcast-message-text-update.component';
import {BroadcastMessageTextDeleteDialogComponent} from './delete/broadcast-message-text-delete-dialog.component';
import {BroadcastMessageTextRoutingModule} from './route/broadcast-message-text-routing.module';

@NgModule({
  imports: [SharedModule, BroadcastMessageTextRoutingModule],
  declarations: [
    BroadcastMessageTextComponent,
    BroadcastMessageTextDetailComponent,
    BroadcastMessageTextUpdateComponent,
    BroadcastMessageTextDeleteDialogComponent,
  ],
  entryComponents: [BroadcastMessageTextDeleteDialogComponent],
})
export class BroadcastMessageTextModule {}
