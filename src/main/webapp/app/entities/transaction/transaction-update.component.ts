import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { ITransaction, Transaction } from 'app/shared/model/transaction.model';
import { TransactionService } from './transaction.service';

@Component({
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html'
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    kind: [null, [Validators.required]],
    bookingDate: [null, [Validators.required]],
    valueDate: [null, [Validators.required]],
    details: [],
    issuer: [null, [Validators.required]],
    recipient: [],
    amount: [null, [Validators.required]],
    balance: [null, [Validators.required]]
  });

  constructor(protected transactionService: TransactionService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      this.updateForm(transaction);
    });
  }

  updateForm(transaction: ITransaction): void {
    this.editForm.patchValue({
      id: transaction.id,
      kind: transaction.kind,
      bookingDate: transaction.bookingDate != null ? transaction.bookingDate.format(DATE_TIME_FORMAT) : null,
      valueDate: transaction.valueDate != null ? transaction.valueDate.format(DATE_TIME_FORMAT) : null,
      details: transaction.details,
      issuer: transaction.issuer,
      recipient: transaction.recipient,
      amount: transaction.amount,
      balance: transaction.balance
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transaction = this.createFromForm();
    if (transaction.id !== undefined) {
      this.subscribeToSaveResponse(this.transactionService.update(transaction));
    } else {
      this.subscribeToSaveResponse(this.transactionService.create(transaction));
    }
  }

  private createFromForm(): ITransaction {
    return {
      ...new Transaction(),
      id: this.editForm.get(['id'])!.value,
      kind: this.editForm.get(['kind'])!.value,
      bookingDate:
        this.editForm.get(['bookingDate'])!.value != null ? moment(this.editForm.get(['bookingDate'])!.value, DATE_TIME_FORMAT) : undefined,
      valueDate:
        this.editForm.get(['valueDate'])!.value != null ? moment(this.editForm.get(['valueDate'])!.value, DATE_TIME_FORMAT) : undefined,
      details: this.editForm.get(['details'])!.value,
      issuer: this.editForm.get(['issuer'])!.value,
      recipient: this.editForm.get(['recipient'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      balance: this.editForm.get(['balance'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
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
