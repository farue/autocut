import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITransactionBook } from '../transaction-book.model';
import { TransactionBookService } from '../service/transaction-book.service';
import { TransactionBookDeleteDialogComponent } from '../delete/transaction-book-delete-dialog.component';

@Component({
  selector: 'jhi-transaction-book',
  templateUrl: './transaction-book.component.html',
})
export class TransactionBookComponent implements OnInit {
  transactionBooks?: ITransactionBook[];
  isLoading = false;

  constructor(protected transactionBookService: TransactionBookService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.transactionBookService.query().subscribe(
      (res: HttpResponse<ITransactionBook[]>) => {
        this.isLoading = false;
        this.transactionBooks = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITransactionBook): number {
    return item.id!;
  }

  delete(transactionBook: ITransactionBook): void {
    const modalRef = this.modalService.open(TransactionBookDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.transactionBook = transactionBook;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
