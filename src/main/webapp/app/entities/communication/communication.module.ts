import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {CommunicationComponent} from './list/communication.component';
import {CommunicationDetailComponent} from './detail/communication-detail.component';
import {CommunicationUpdateComponent} from './update/communication-update.component';
import {CommunicationDeleteDialogComponent} from './delete/communication-delete-dialog.component';
import {CommunicationRoutingModule} from './route/communication-routing.module';

@NgModule({
  imports: [SharedModule, CommunicationRoutingModule],
  declarations: [CommunicationComponent, CommunicationDetailComponent, CommunicationUpdateComponent, CommunicationDeleteDialogComponent],
  entryComponents: [CommunicationDeleteDialogComponent],
})
export class CommunicationModule {}
