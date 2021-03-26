import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommunication } from '../communication.model';
import { CommunicationService } from '../service/communication.service';

@Component({
  templateUrl: './communication-delete-dialog.component.html',
})
export class CommunicationDeleteDialogComponent {
  communication?: ICommunication;

  constructor(protected communicationService: CommunicationService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.communicationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
