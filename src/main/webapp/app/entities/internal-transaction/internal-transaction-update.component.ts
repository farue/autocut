import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_WITH_MILLIS_FORMAT } from 'app/shared/constants/input.constants';

import { IInternalTransaction, InternalTransaction } from 'app/shared/model/internal-transaction.model';
import { InternalTransactionService } from './internal-transaction.service';
import { ITransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/transaction-book.service';

type SelectableEntity = IInternalTransaction | ITransactionBook;

@Component({
  selector: 'jhi-internal-transaction-update',
  templateUrl: './internal-transaction-update.component.html',
})
export class InternalTransactionUpdateComponent implements OnInit {
  isSaving = false;
  internaltransactions: IInternalTransaction[] = [];
  transactionbooks: ITransactionBook[] = [];

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required]],
    bookingDate: [null, [Validators.required]],
    valueDate: [null, [Validators.required]],
    value: [null, [Validators.required]],
    balanceAfter: [null, [Validators.required]],
    description: [],
    serviceQulifier: [],
    issuer: [null, [Validators.required]],
    recipient: [],
    lefts: [],
    transactionBook: [null, Validators.required],
  });

  constructor(
    protected internalTransactionService: InternalTransactionService,
    protected transactionBookService: TransactionBookService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ internalTransaction }) => {
      if (!internalTransaction.id) {
        const today = moment().startOf('day');
        internalTransaction.bookingDate = today;
        internalTransaction.valueDate = today;
      }

      this.updateForm(internalTransaction);

      this.internalTransactionService
        .query()
        .subscribe((res: HttpResponse<IInternalTransaction[]>) => (this.internaltransactions = res.body || []));

      this.transactionBookService.query().subscribe((res: HttpResponse<ITransactionBook[]>) => (this.transactionbooks = res.body || []));
    });
  }

  updateForm(internalTransaction: IInternalTransaction): void {
    this.editForm.patchValue({
      id: internalTransaction.id,
      type: internalTransaction.type,
      bookingDate: internalTransaction.bookingDate ? internalTransaction.bookingDate.format(DATE_TIME_WITH_MILLIS_FORMAT) : null,
      valueDate: internalTransaction.valueDate ? internalTransaction.valueDate.format(DATE_TIME_WITH_MILLIS_FORMAT) : null,
      value: internalTransaction.value,
      balanceAfter: internalTransaction.balanceAfter,
      description: internalTransaction.description,
      serviceQulifier: internalTransaction.serviceQulifier,
      issuer: internalTransaction.issuer,
      recipient: internalTransaction.recipient,
      lefts: internalTransaction.lefts,
      transactionBook: internalTransaction.transactionBook,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const internalTransaction = this.createFromForm();
    if (internalTransaction.id !== undefined) {
      this.subscribeToSaveResponse(this.internalTransactionService.update(internalTransaction));
    } else {
      this.subscribeToSaveResponse(this.internalTransactionService.create(internalTransaction));
    }
  }

  private createFromForm(): IInternalTransaction {
    return {
      ...new InternalTransaction(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      bookingDate: this.editForm.get(['bookingDate'])!.value
        ? moment(this.editForm.get(['bookingDate'])!.value, DATE_TIME_WITH_MILLIS_FORMAT)
        : undefined,
      valueDate: this.editForm.get(['valueDate'])!.value
        ? moment(this.editForm.get(['valueDate'])!.value, DATE_TIME_WITH_MILLIS_FORMAT)
        : undefined,
      value: this.editForm.get(['value'])!.value,
      balanceAfter: this.editForm.get(['balanceAfter'])!.value,
      description: this.editForm.get(['description'])!.value,
      serviceQulifier: this.editForm.get(['serviceQulifier'])!.value,
      issuer: this.editForm.get(['issuer'])!.value,
      recipient: this.editForm.get(['recipient'])!.value,
      lefts: this.editForm.get(['lefts'])!.value,
      transactionBook: this.editForm.get(['transactionBook'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInternalTransaction>>): void {
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

  getSelected(selectedVals: IInternalTransaction[], option: IInternalTransaction): IInternalTransaction {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
