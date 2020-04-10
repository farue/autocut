import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IWashHistory } from 'app/shared/model/wash-history.model';
import { WashHistoryService } from './wash-history.service';

@Component({
  templateUrl: './wash-history-delete-dialog.component.html'
})
export class WashHistoryDeleteDialogComponent {
  washHistory?: IWashHistory;

  constructor(
    protected washHistoryService: WashHistoryService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.washHistoryService.delete(id).subscribe(() => {
      this.eventManager.broadcast('washHistoryListModification');
      this.activeModal.close();
    });
  }
}
