import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITransactionBook } from '../transaction-book.model';

@Component({
  selector: 'jhi-transaction-book-detail',
  templateUrl: './transaction-book-detail.component.html',
})
export class TransactionBookDetailComponent implements OnInit {
  transactionBook: ITransactionBook | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transactionBook }) => {
      this.transactionBook = transactionBook;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
