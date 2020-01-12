import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPaymentEntry } from 'app/shared/model/payment-entry.model';

@Component({
  selector: 'jhi-payment-entry-detail',
  templateUrl: './payment-entry-detail.component.html'
})
export class PaymentEntryDetailComponent implements OnInit {
  paymentEntry: IPaymentEntry | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentEntry }) => {
      this.paymentEntry = paymentEntry;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
