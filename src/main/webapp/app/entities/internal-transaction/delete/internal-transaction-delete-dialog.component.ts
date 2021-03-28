import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IInternalTransaction } from '../internal-transaction.model';
import { InternalTransactionService } from '../service/internal-transaction.service';

@Component({
  templateUrl: './internal-transaction-delete-dialog.component.html',
})
export class InternalTransactionDeleteDialogComponent {
  internalTransaction?: IInternalTransaction;

  constructor(protected internalTransactionService: InternalTransactionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.internalTransactionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
