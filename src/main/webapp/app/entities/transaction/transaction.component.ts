import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ITransaction } from 'app/shared/model/transaction.model';
import { TransactionService } from './transaction.service';

@Component({
  selector: 'jhi-transaction',
  templateUrl: './transaction.component.html'
})
export class TransactionComponent implements OnInit, OnDestroy {
  transactions: ITransaction[];
  eventSubscriber: Subscription;

  constructor(protected transactionService: TransactionService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.transactionService.query().subscribe((res: HttpResponse<ITransaction[]>) => {
      this.transactions = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInTransactions();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ITransaction) {
    return item.id;
  }

  registerChangeInTransactions() {
    this.eventSubscriber = this.eventManager.subscribe('transactionListModification', () => this.loadAll());
  }
}
