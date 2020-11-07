import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IInternalTransaction } from 'app/shared/model/internal-transaction.model';
import { InternalTransactionService } from './internal-transaction.service';

@Component({
  templateUrl: './internal-transaction-delete-dialog.component.html',
})
export class InternalTransactionDeleteDialogComponent {
  internalTransaction?: IInternalTransaction;

  constructor(
    protected internalTransactionService: InternalTransactionService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.internalTransactionService.delete(id).subscribe(() => {
      this.eventManager.broadcast('internalTransactionListModification');
      this.activeModal.close();
    });
  }
}
