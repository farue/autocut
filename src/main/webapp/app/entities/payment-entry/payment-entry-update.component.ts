import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPaymentEntry, PaymentEntry } from 'app/shared/model/payment-entry.model';
import { PaymentEntryService } from './payment-entry.service';
import { ITransaction } from 'app/shared/model/transaction.model';
import { TransactionService } from 'app/entities/transaction/transaction.service';
import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from 'app/entities/payment-account/payment-account.service';

type SelectableEntity = ITransaction | IPaymentAccount;

@Component({
  selector: 'jhi-payment-entry-update',
  templateUrl: './payment-entry-update.component.html'
})
export class PaymentEntryUpdateComponent implements OnInit {
  isSaving = false;

  transactions: ITransaction[] = [];

  paymentaccounts: IPaymentAccount[] = [];

  editForm = this.fb.group({
    id: [],
    balanceBefore: [null, [Validators.required]],
    balanceAfter: [null, [Validators.required]],
    payment: [null, [Validators.required]],
    date: [null, [Validators.required]],
    description: [],
    transaction: [],
    account: []
  });

  constructor(
    protected paymentEntryService: PaymentEntryService,
    protected transactionService: TransactionService,
    protected paymentAccountService: PaymentAccountService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentEntry }) => {
      this.updateForm(paymentEntry);

      this.transactionService
        .query({ filter: 'paymententry-is-null' })
        .pipe(
          map((res: HttpResponse<ITransaction[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ITransaction[]) => {
          if (!paymentEntry.transaction || !paymentEntry.transaction.id) {
            this.transactions = resBody;
          } else {
            this.transactionService
              .find(paymentEntry.transaction.id)
              .pipe(
                map((subRes: HttpResponse<ITransaction>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: ITransaction[]) => {
                this.transactions = concatRes;
              });
          }
        });

      this.paymentAccountService
        .query()
        .pipe(
          map((res: HttpResponse<IPaymentAccount[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IPaymentAccount[]) => (this.paymentaccounts = resBody));
    });
  }

  updateForm(paymentEntry: IPaymentEntry): void {
    this.editForm.patchValue({
      id: paymentEntry.id,
      balanceBefore: paymentEntry.balanceBefore,
      balanceAfter: paymentEntry.balanceAfter,
      payment: paymentEntry.payment,
      date: paymentEntry.date != null ? paymentEntry.date.format(DATE_TIME_FORMAT) : null,
      description: paymentEntry.description,
      transaction: paymentEntry.transaction,
      account: paymentEntry.account
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const paymentEntry = this.createFromForm();
    if (paymentEntry.id !== undefined) {
      this.subscribeToSaveResponse(this.paymentEntryService.update(paymentEntry));
    } else {
      this.subscribeToSaveResponse(this.paymentEntryService.create(paymentEntry));
    }
  }

  private createFromForm(): IPaymentEntry {
    return {
      ...new PaymentEntry(),
      id: this.editForm.get(['id'])!.value,
      balanceBefore: this.editForm.get(['balanceBefore'])!.value,
      balanceAfter: this.editForm.get(['balanceAfter'])!.value,
      payment: this.editForm.get(['payment'])!.value,
      date: this.editForm.get(['date'])!.value != null ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      description: this.editForm.get(['description'])!.value,
      transaction: this.editForm.get(['transaction'])!.value,
      account: this.editForm.get(['account'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaymentEntry>>): void {
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

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
