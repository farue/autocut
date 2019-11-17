import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { IPaymentAccount, PaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';
import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from 'app/entities/lease/lease.service';

@Component({
  selector: 'jhi-payment-account-update',
  templateUrl: './payment-account-update.component.html'
})
export class PaymentAccountUpdateComponent implements OnInit {
  isSaving: boolean;

  leases: ILease[];

  editForm = this.fb.group({
    id: [],
    balance: [null, [Validators.required]]
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected paymentAccountService: PaymentAccountService,
    protected leaseService: LeaseService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ paymentAccount }) => {
      this.updateForm(paymentAccount);
    });
    this.leaseService
      .query()
      .subscribe((res: HttpResponse<ILease[]>) => (this.leases = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(paymentAccount: IPaymentAccount) {
    this.editForm.patchValue({
      id: paymentAccount.id,
      balance: paymentAccount.balance
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const paymentAccount = this.createFromForm();
    if (paymentAccount.id !== undefined) {
      this.subscribeToSaveResponse(this.paymentAccountService.update(paymentAccount));
    } else {
      this.subscribeToSaveResponse(this.paymentAccountService.create(paymentAccount));
    }
  }

  private createFromForm(): IPaymentAccount {
    return {
      ...new PaymentAccount(),
      id: this.editForm.get(['id']).value,
      balance: this.editForm.get(['balance']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaymentAccount>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackLeaseById(index: number, item: ILease) {
    return item.id;
  }
}
