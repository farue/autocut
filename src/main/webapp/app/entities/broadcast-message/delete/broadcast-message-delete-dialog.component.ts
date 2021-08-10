import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBroadcastMessage } from '../broadcast-message.model';
import { BroadcastMessageService } from '../service/broadcast-message.service';

@Component({
  templateUrl: './broadcast-message-delete-dialog.component.html',
})
export class BroadcastMessageDeleteDialogComponent {
  broadcastMessage?: IBroadcastMessage;

  constructor(protected broadcastMessageService: BroadcastMessageService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.broadcastMessageService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
