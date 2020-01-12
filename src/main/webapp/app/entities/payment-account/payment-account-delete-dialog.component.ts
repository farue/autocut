import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';

@Component({
  templateUrl: './payment-account-delete-dialog.component.html'
})
export class PaymentAccountDeleteDialogComponent {
  paymentAccount?: IPaymentAccount;

  constructor(
    protected paymentAccountService: PaymentAccountService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paymentAccountService.delete(id).subscribe(() => {
      this.eventManager.broadcast('paymentAccountListModification');
      this.activeModal.close();
    });
  }
}
