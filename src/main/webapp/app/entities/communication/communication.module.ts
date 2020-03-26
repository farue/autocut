import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { CommunicationComponent } from './communication.component';
import { CommunicationDetailComponent } from './communication-detail.component';
import { CommunicationUpdateComponent } from './communication-update.component';
import { CommunicationDeleteDialogComponent } from './communication-delete-dialog.component';
import { communicationRoute } from './communication.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(communicationRoute)],
  declarations: [CommunicationComponent, CommunicationDetailComponent, CommunicationUpdateComponent, CommunicationDeleteDialogComponent],
  entryComponents: [CommunicationDeleteDialogComponent]
})
export class AutocutCommunicationModule {}
