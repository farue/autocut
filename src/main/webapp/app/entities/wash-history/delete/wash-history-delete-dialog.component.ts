import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IWashHistory } from '../wash-history.model';
import { WashHistoryService } from '../service/wash-history.service';

@Component({
  templateUrl: './wash-history-delete-dialog.component.html',
})
export class WashHistoryDeleteDialogComponent {
  washHistory?: IWashHistory;

  constructor(protected washHistoryService: WashHistoryService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.washHistoryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
