import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IInternalTransaction } from '../internal-transaction.model';
import { InternalTransactionService } from '../service/internal-transaction.service';
import { InternalTransactionDeleteDialogComponent } from '../delete/internal-transaction-delete-dialog.component';

@Component({
  selector: 'jhi-internal-transaction',
  templateUrl: './internal-transaction.component.html',
})
export class InternalTransactionComponent implements OnInit {
  internalTransactions?: IInternalTransaction[];
  isLoading = false;

  constructor(protected internalTransactionService: InternalTransactionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.internalTransactionService.query().subscribe(
      (res: HttpResponse<IInternalTransaction[]>) => {
        this.isLoading = false;
        this.internalTransactions = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IInternalTransaction): number {
    return item.id!;
  }

  delete(internalTransaction: IInternalTransaction): void {
    const modalRef = this.modalService.open(InternalTransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.internalTransaction = internalTransaction;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
