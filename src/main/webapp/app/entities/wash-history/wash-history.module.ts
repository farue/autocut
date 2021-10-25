import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WashHistoryComponent } from './list/wash-history.component';
import { WashHistoryDetailComponent } from './detail/wash-history-detail.component';
import { WashHistoryUpdateComponent } from './update/wash-history-update.component';
import { WashHistoryDeleteDialogComponent } from './delete/wash-history-delete-dialog.component';
import { WashHistoryRoutingModule } from './route/wash-history-routing.module';

@NgModule({
  imports: [SharedModule, WashHistoryRoutingModule],
  declarations: [WashHistoryComponent, WashHistoryDetailComponent, WashHistoryUpdateComponent, WashHistoryDeleteDialogComponent],
  entryComponents: [WashHistoryDeleteDialogComponent],
})
export class WashHistoryModule {}
