import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInternalTransaction } from '../internal-transaction.model';

@Component({
  selector: 'jhi-internal-transaction-detail',
  templateUrl: './internal-transaction-detail.component.html',
})
export class InternalTransactionDetailComponent implements OnInit {
  internalTransaction: IInternalTransaction | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ internalTransaction }) => {
      this.internalTransaction = internalTransaction;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
