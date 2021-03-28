import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IInternalTransaction, InternalTransaction } from '../internal-transaction.model';
import { InternalTransactionService } from '../service/internal-transaction.service';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';

@Component({
  selector: 'jhi-internal-transaction-update',
  templateUrl: './internal-transaction-update.component.html',
})
export class InternalTransactionUpdateComponent implements OnInit {
  isSaving = false;

  internalTransactionsSharedCollection: IInternalTransaction[] = [];
  transactionBooksSharedCollection: ITransactionBook[] = [];

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
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ internalTransaction }) => {
      if (internalTransaction.id === undefined) {
        const today = dayjs().startOf('day');
        internalTransaction.bookingDate = today;
        internalTransaction.valueDate = today;
      }

      this.updateForm(internalTransaction);

      this.loadRelationshipsOptions();
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

  trackInternalTransactionById(index: number, item: IInternalTransaction): number {
    return item.id!;
  }

  trackTransactionBookById(index: number, item: ITransactionBook): number {
    return item.id!;
  }

  getSelectedInternalTransaction(option: IInternalTransaction, selectedVals?: IInternalTransaction[]): IInternalTransaction {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInternalTransaction>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(internalTransaction: IInternalTransaction): void {
    this.editForm.patchValue({
      id: internalTransaction.id,
      type: internalTransaction.type,
      bookingDate: internalTransaction.bookingDate ? internalTransaction.bookingDate.format(DATE_TIME_FORMAT) : null,
      valueDate: internalTransaction.valueDate ? internalTransaction.valueDate.format(DATE_TIME_FORMAT) : null,
      value: internalTransaction.value,
      balanceAfter: internalTransaction.balanceAfter,
      description: internalTransaction.description,
      serviceQulifier: internalTransaction.serviceQulifier,
      issuer: internalTransaction.issuer,
      recipient: internalTransaction.recipient,
      lefts: internalTransaction.lefts,
      transactionBook: internalTransaction.transactionBook,
    });

    this.internalTransactionsSharedCollection = this.internalTransactionService.addInternalTransactionToCollectionIfMissing(
      this.internalTransactionsSharedCollection,
      ...(internalTransaction.lefts ?? [])
    );
    this.transactionBooksSharedCollection = this.transactionBookService.addTransactionBookToCollectionIfMissing(
      this.transactionBooksSharedCollection,
      internalTransaction.transactionBook
    );
  }

  protected loadRelationshipsOptions(): void {
    this.internalTransactionService
      .query()
      .pipe(map((res: HttpResponse<IInternalTransaction[]>) => res.body ?? []))
      .pipe(
        map((internalTransactions: IInternalTransaction[]) =>
          this.internalTransactionService.addInternalTransactionToCollectionIfMissing(
            internalTransactions,
            ...(this.editForm.get('lefts')!.value ?? [])
          )
        )
      )
      .subscribe((internalTransactions: IInternalTransaction[]) => (this.internalTransactionsSharedCollection = internalTransactions));

    this.transactionBookService
      .query()
      .pipe(map((res: HttpResponse<ITransactionBook[]>) => res.body ?? []))
      .pipe(
        map((transactionBooks: ITransactionBook[]) =>
          this.transactionBookService.addTransactionBookToCollectionIfMissing(transactionBooks, this.editForm.get('transactionBook')!.value)
        )
      )
      .subscribe((transactionBooks: ITransactionBook[]) => (this.transactionBooksSharedCollection = transactionBooks));
  }

  protected createFromForm(): IInternalTransaction {
    return {
      ...new InternalTransaction(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      bookingDate: this.editForm.get(['bookingDate'])!.value
        ? dayjs(this.editForm.get(['bookingDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      valueDate: this.editForm.get(['valueDate'])!.value ? dayjs(this.editForm.get(['valueDate'])!.value, DATE_TIME_FORMAT) : undefined,
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
}
