import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';

@Component({
  selector: 'jhi-payment-account',
  templateUrl: './payment-account.component.html'
})
export class PaymentAccountComponent implements OnInit, OnDestroy {
  paymentAccounts: IPaymentAccount[];
  eventSubscriber: Subscription;

  constructor(protected paymentAccountService: PaymentAccountService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.paymentAccountService.query().subscribe((res: HttpResponse<IPaymentAccount[]>) => {
      this.paymentAccounts = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInPaymentAccounts();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IPaymentAccount) {
    return item.id;
  }

  registerChangeInPaymentAccounts() {
    this.eventSubscriber = this.eventManager.subscribe('paymentAccountListModification', () => this.loadAll());
  }
}
