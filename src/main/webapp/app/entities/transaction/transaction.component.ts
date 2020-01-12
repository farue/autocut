import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITransaction } from 'app/shared/model/transaction.model';
import { TransactionService } from './transaction.service';
import { TransactionDeleteDialogComponent } from './transaction-delete-dialog.component';

@Component({
  selector: 'jhi-transaction',
  templateUrl: './transaction.component.html'
})
export class TransactionComponent implements OnInit, OnDestroy {
  transactions?: ITransaction[];
  eventSubscriber?: Subscription;

  constructor(
    protected transactionService: TransactionService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.transactionService.query().subscribe((res: HttpResponse<ITransaction[]>) => {
      this.transactions = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTransactions();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITransaction): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTransactions(): void {
    this.eventSubscriber = this.eventManager.subscribe('transactionListModification', () => this.loadAll());
  }

  delete(transaction: ITransaction): void {
    const modalRef = this.modalService.open(TransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.transaction = transaction;
  }
}
