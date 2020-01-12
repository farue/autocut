import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPaymentEntry } from 'app/shared/model/payment-entry.model';
import { PaymentEntryService } from './payment-entry.service';

@Component({
  templateUrl: './payment-entry-delete-dialog.component.html'
})
export class PaymentEntryDeleteDialogComponent {
  paymentEntry?: IPaymentEntry;

  constructor(
    protected paymentEntryService: PaymentEntryService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paymentEntryService.delete(id).subscribe(() => {
      this.eventManager.broadcast('paymentEntryListModification');
      this.activeModal.close();
    });
  }
}
