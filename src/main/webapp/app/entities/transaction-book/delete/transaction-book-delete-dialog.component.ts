import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITransactionBook } from '../transaction-book.model';
import { TransactionBookService } from '../service/transaction-book.service';

@Component({
  templateUrl: './transaction-book-delete-dialog.component.html',
})
export class TransactionBookDeleteDialogComponent {
  transactionBook?: ITransactionBook;

  constructor(protected transactionBookService: TransactionBookService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.transactionBookService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
