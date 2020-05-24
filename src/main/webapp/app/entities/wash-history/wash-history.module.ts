import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { WashHistoryComponent } from './wash-history.component';
import { WashHistoryDetailComponent } from './wash-history-detail.component';
import { WashHistoryUpdateComponent } from './wash-history-update.component';
import { WashHistoryDeleteDialogComponent } from './wash-history-delete-dialog.component';
import { washHistoryRoute } from './wash-history.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(washHistoryRoute)],
  declarations: [WashHistoryComponent, WashHistoryDetailComponent, WashHistoryUpdateComponent, WashHistoryDeleteDialogComponent],
  entryComponents: [WashHistoryDeleteDialogComponent],
})
export class AutocutWashHistoryModule {}
