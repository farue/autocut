import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPaymentAccount } from 'app/shared/model/payment-account.model';

@Component({
  selector: 'jhi-payment-account-detail',
  templateUrl: './payment-account-detail.component.html'
})
export class PaymentAccountDetailComponent implements OnInit {
  paymentAccount: IPaymentAccount | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentAccount }) => {
      this.paymentAccount = paymentAccount;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
