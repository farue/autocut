import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionBookService } from './transaction-book.service';

@Component({
  templateUrl: './transaction-book-delete-dialog.component.html',
})
export class TransactionBookDeleteDialogComponent {
  transactionBook?: ITransactionBook;

  constructor(
    protected transactionBookService: TransactionBookService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.transactionBookService.delete(id).subscribe(() => {
      this.eventManager.broadcast('transactionBookListModification');
      this.activeModal.close();
    });
  }
}
