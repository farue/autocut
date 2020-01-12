import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IPaymentAccount, PaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from './payment-account.service';

@Component({
  selector: 'jhi-payment-account-update',
  templateUrl: './payment-account-update.component.html'
})
export class PaymentAccountUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    balance: [null, [Validators.required]]
  });

  constructor(protected paymentAccountService: PaymentAccountService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentAccount }) => {
      this.updateForm(paymentAccount);
    });
  }

  updateForm(paymentAccount: IPaymentAccount): void {
    this.editForm.patchValue({
      id: paymentAccount.id,
      balance: paymentAccount.balance
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
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
      id: this.editForm.get(['id'])!.value,
      balance: this.editForm.get(['balance'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaymentAccount>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
