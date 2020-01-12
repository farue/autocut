import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPaymentEntry } from 'app/shared/model/payment-entry.model';
import { PaymentEntryService } from './payment-entry.service';
import { PaymentEntryDeleteDialogComponent } from './payment-entry-delete-dialog.component';

@Component({
  selector: 'jhi-payment-entry',
  templateUrl: './payment-entry.component.html'
})
export class PaymentEntryComponent implements OnInit, OnDestroy {
  paymentEntries?: IPaymentEntry[];
  eventSubscriber?: Subscription;

  constructor(
    protected paymentEntryService: PaymentEntryService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.paymentEntryService.query().subscribe((res: HttpResponse<IPaymentEntry[]>) => {
      this.paymentEntries = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPaymentEntries();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPaymentEntry): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInPaymentEntries(): void {
    this.eventSubscriber = this.eventManager.subscribe('paymentEntryListModification', () => this.loadAll());
  }

  delete(paymentEntry: IPaymentEntry): void {
    const modalRef = this.modalService.open(PaymentEntryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.paymentEntry = paymentEntry;
  }
}
