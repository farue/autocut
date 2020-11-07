import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBankTransaction } from 'app/shared/model/bank-transaction.model';
import { BankTransactionService } from './bank-transaction.service';

@Component({
  templateUrl: './bank-transaction-delete-dialog.component.html',
})
export class BankTransactionDeleteDialogComponent {
  bankTransaction?: IBankTransaction;

  constructor(
    protected bankTransactionService: BankTransactionService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bankTransactionService.delete(id).subscribe(() => {
      this.eventManager.broadcast('bankTransactionListModification');
      this.activeModal.close();
    });
  }
}
