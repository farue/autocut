import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IBroadcastMessageText} from '../broadcast-message-text.model';
import {BroadcastMessageTextService} from '../service/broadcast-message-text.service';

@Component({
  templateUrl: './broadcast-message-text-delete-dialog.component.html',
})
export class BroadcastMessageTextDeleteDialogComponent {
  broadcastMessageText?: IBroadcastMessageText;

  constructor(protected broadcastMessageTextService: BroadcastMessageTextService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.broadcastMessageTextService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
