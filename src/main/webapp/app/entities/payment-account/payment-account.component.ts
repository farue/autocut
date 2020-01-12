import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';
import { PaymentAccountDeleteDialogComponent } from './payment-account-delete-dialog.component';

@Component({
  selector: 'jhi-payment-account',
  templateUrl: './payment-account.component.html'
})
export class PaymentAccountComponent implements OnInit, OnDestroy {
  paymentAccounts?: IPaymentAccount[];
  eventSubscriber?: Subscription;

  constructor(
    protected paymentAccountService: PaymentAccountService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.paymentAccountService.query().subscribe((res: HttpResponse<IPaymentAccount[]>) => {
      this.paymentAccounts = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPaymentAccounts();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPaymentAccount): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInPaymentAccounts(): void {
    this.eventSubscriber = this.eventManager.subscribe('paymentAccountListModification', () => this.loadAll());
  }

  delete(paymentAccount: IPaymentAccount): void {
    const modalRef = this.modalService.open(PaymentAccountDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.paymentAccount = paymentAccount;
  }
}
