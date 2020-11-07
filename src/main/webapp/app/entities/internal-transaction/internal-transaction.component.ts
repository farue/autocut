import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IInternalTransaction } from 'app/shared/model/internal-transaction.model';
import { InternalTransactionService } from './internal-transaction.service';
import { InternalTransactionDeleteDialogComponent } from './internal-transaction-delete-dialog.component';

@Component({
  selector: 'jhi-internal-transaction',
  templateUrl: './internal-transaction.component.html',
})
export class InternalTransactionComponent implements OnInit, OnDestroy {
  internalTransactions?: IInternalTransaction[];
  eventSubscriber?: Subscription;

  constructor(
    protected internalTransactionService: InternalTransactionService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.internalTransactionService
      .query()
      .subscribe((res: HttpResponse<IInternalTransaction[]>) => (this.internalTransactions = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInInternalTransactions();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IInternalTransaction): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInInternalTransactions(): void {
    this.eventSubscriber = this.eventManager.subscribe('internalTransactionListModification', () => this.loadAll());
  }

  delete(internalTransaction: IInternalTransaction): void {
    const modalRef = this.modalService.open(InternalTransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.internalTransaction = internalTransaction;
  }
}
