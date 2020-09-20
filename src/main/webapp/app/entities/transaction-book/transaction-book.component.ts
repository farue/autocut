import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionBookService } from './transaction-book.service';
import { TransactionBookDeleteDialogComponent } from './transaction-book-delete-dialog.component';

@Component({
  selector: 'jhi-transaction-book',
  templateUrl: './transaction-book.component.html',
})
export class TransactionBookComponent implements OnInit, OnDestroy {
  transactionBooks?: ITransactionBook[];
  eventSubscriber?: Subscription;

  constructor(
    protected transactionBookService: TransactionBookService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.transactionBookService.query().subscribe((res: HttpResponse<ITransactionBook[]>) => (this.transactionBooks = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTransactionBooks();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITransactionBook): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTransactionBooks(): void {
    this.eventSubscriber = this.eventManager.subscribe('transactionBookListModification', () => this.loadAll());
  }

  delete(transactionBook: ITransactionBook): void {
    const modalRef = this.modalService.open(TransactionBookDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.transactionBook = transactionBook;
  }
}
