import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';

@Component({
  selector: 'jhi-payment-account-delete-dialog',
  templateUrl: './payment-account-delete-dialog.component.html'
})
export class PaymentAccountDeleteDialogComponent {
  paymentAccount: IPaymentAccount;

  constructor(
    protected paymentAccountService: PaymentAccountService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.paymentAccountService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'paymentAccountListModification',
        content: 'Deleted an paymentAccount'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-payment-account-delete-popup',
  template: ''
})
export class PaymentAccountDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ paymentAccount }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(PaymentAccountDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.paymentAccount = paymentAccount;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/payment-account', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/payment-account', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
