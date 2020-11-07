import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBankTransaction } from 'app/shared/model/bank-transaction.model';
import { BankTransactionService } from './bank-transaction.service';
import { BankTransactionDeleteDialogComponent } from './bank-transaction-delete-dialog.component';

@Component({
  selector: 'jhi-bank-transaction',
  templateUrl: './bank-transaction.component.html',
})
export class BankTransactionComponent implements OnInit, OnDestroy {
  bankTransactions?: IBankTransaction[];
  eventSubscriber?: Subscription;

  constructor(
    protected bankTransactionService: BankTransactionService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.bankTransactionService.query().subscribe((res: HttpResponse<IBankTransaction[]>) => (this.bankTransactions = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInBankTransactions();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IBankTransaction): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInBankTransactions(): void {
    this.eventSubscriber = this.eventManager.subscribe('bankTransactionListModification', () => this.loadAll());
  }

  delete(bankTransaction: IBankTransaction): void {
    const modalRef = this.modalService.open(BankTransactionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.bankTransaction = bankTransaction;
  }
}
