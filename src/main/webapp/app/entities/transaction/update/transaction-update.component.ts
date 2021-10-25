import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ITransaction, Transaction } from '../transaction.model';
import { TransactionService } from '../service/transaction.service';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';
import { TransactionKind } from 'app/entities/enumerations/transaction-kind.model';

@Component({
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html',
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;
  transactionKindValues = Object.keys(TransactionKind);

  transactionsSharedCollection: ITransaction[] = [];
  transactionBooksSharedCollection: ITransactionBook[] = [];

  editForm = this.fb.group({
    id: [],
    kind: [null, [Validators.required]],
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
    protected transactionService: TransactionService,
    protected transactionBookService: TransactionBookService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      if (transaction.id === undefined) {
        const today = dayjs().startOf('day');
        transaction.bookingDate = today;
        transaction.valueDate = today;
      }

      this.updateForm(transaction);

      this.loadRelationshipsOptions();
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

  trackTransactionById(index: number, item: ITransaction): number {
    return item.id!;
  }

  trackTransactionBookById(index: number, item: ITransactionBook): number {
    return item.id!;
  }

  getSelectedTransaction(option: ITransaction, selectedVals?: ITransaction[]): ITransaction {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
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

  protected updateForm(transaction: ITransaction): void {
    this.editForm.patchValue({
      id: transaction.id,
      kind: transaction.kind,
      bookingDate: transaction.bookingDate ? transaction.bookingDate.format(DATE_TIME_FORMAT) : null,
      valueDate: transaction.valueDate ? transaction.valueDate.format(DATE_TIME_FORMAT) : null,
      value: transaction.value,
      balanceAfter: transaction.balanceAfter,
      description: transaction.description,
      serviceQulifier: transaction.serviceQulifier,
      issuer: transaction.issuer,
      recipient: transaction.recipient,
      lefts: transaction.lefts,
      transactionBook: transaction.transactionBook,
    });

    this.transactionsSharedCollection = this.transactionService.addTransactionToCollectionIfMissing(
      this.transactionsSharedCollection,
      ...(transaction.lefts ?? [])
    );
    this.transactionBooksSharedCollection = this.transactionBookService.addTransactionBookToCollectionIfMissing(
      this.transactionBooksSharedCollection,
      transaction.transactionBook
    );
  }

  protected loadRelationshipsOptions(): void {
    this.transactionService
      .query()
      .pipe(map((res: HttpResponse<ITransaction[]>) => res.body ?? []))
      .pipe(
        map((transactions: ITransaction[]) =>
          this.transactionService.addTransactionToCollectionIfMissing(transactions, ...(this.editForm.get('lefts')!.value ?? []))
        )
      )
      .subscribe((transactions: ITransaction[]) => (this.transactionsSharedCollection = transactions));

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

  protected createFromForm(): ITransaction {
    return {
      ...new Transaction(),
      id: this.editForm.get(['id'])!.value,
      kind: this.editForm.get(['kind'])!.value,
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
