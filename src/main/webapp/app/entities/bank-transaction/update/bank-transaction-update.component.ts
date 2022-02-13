import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { BankTransaction, IBankTransaction } from '../bank-transaction.model';
import { BankTransactionService } from '../service/bank-transaction.service';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';

@Component({
  selector: 'jhi-bank-transaction-update',
  templateUrl: './bank-transaction-update.component.html',
})
export class BankTransactionUpdateComponent implements OnInit {
  isSaving = false;

  bankAccountsSharedCollection: IBankAccount[] = [];
  bankTransactionsSharedCollection: IBankTransaction[] = [];
  transactionBooksSharedCollection: ITransactionBook[] = [];

  editForm = this.fb.group({
    id: [],
    bookingDate: [null, [Validators.required]],
    valueDate: [null, [Validators.required]],
    value: [null, [Validators.required]],
    balanceAfter: [null, [Validators.required]],
    type: [],
    description: [],
    customerRef: [],
    gvCode: [],
    endToEnd: [],
    primanota: [],
    creditor: [],
    mandate: [],
    bankAccount: [null, Validators.required],
    contraBankAccount: [],
    lefts: [],
    transactionBook: [null, Validators.required],
  });

  constructor(
    protected bankTransactionService: BankTransactionService,
    protected bankAccountService: BankAccountService,
    protected transactionBookService: TransactionBookService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankTransaction }) => {
      if (bankTransaction.id === undefined) {
        const today = dayjs().startOf('day');
        bankTransaction.bookingDate = today;
        bankTransaction.valueDate = today;
      }

      this.updateForm(bankTransaction);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bankTransaction = this.createFromForm();
    if (bankTransaction.id !== undefined) {
      this.subscribeToSaveResponse(this.bankTransactionService.update(bankTransaction));
    } else {
      this.subscribeToSaveResponse(this.bankTransactionService.create(bankTransaction));
    }
  }

  trackBankAccountById(index: number, item: IBankAccount): number {
    return item.id!;
  }

  trackBankTransactionById(index: number, item: IBankTransaction): number {
    return item.id!;
  }

  trackTransactionBookById(index: number, item: ITransactionBook): number {
    return item.id!;
  }

  getSelectedBankTransaction(option: IBankTransaction, selectedVals?: IBankTransaction[]): IBankTransaction {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBankTransaction>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
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

  protected updateForm(bankTransaction: IBankTransaction): void {
    this.editForm.patchValue({
      id: bankTransaction.id,
      bookingDate: bankTransaction.bookingDate ? bankTransaction.bookingDate.format(DATE_TIME_FORMAT) : null,
      valueDate: bankTransaction.valueDate ? bankTransaction.valueDate.format(DATE_TIME_FORMAT) : null,
      value: bankTransaction.value,
      balanceAfter: bankTransaction.balanceAfter,
      type: bankTransaction.type,
      description: bankTransaction.description,
      customerRef: bankTransaction.customerRef,
      gvCode: bankTransaction.gvCode,
      endToEnd: bankTransaction.endToEnd,
      primanota: bankTransaction.primanota,
      creditor: bankTransaction.creditor,
      mandate: bankTransaction.mandate,
      bankAccount: bankTransaction.bankAccount,
      contraBankAccount: bankTransaction.contraBankAccount,
      lefts: bankTransaction.lefts,
      transactionBook: bankTransaction.transactionBook,
    });

    this.bankAccountsSharedCollection = this.bankAccountService.addBankAccountToCollectionIfMissing(
      this.bankAccountsSharedCollection,
      bankTransaction.bankAccount,
      bankTransaction.contraBankAccount
    );
    this.bankTransactionsSharedCollection = this.bankTransactionService.addBankTransactionToCollectionIfMissing(
      this.bankTransactionsSharedCollection,
      ...(bankTransaction.lefts ?? [])
    );
    this.transactionBooksSharedCollection = this.transactionBookService.addTransactionBookToCollectionIfMissing(
      this.transactionBooksSharedCollection,
      bankTransaction.transactionBook
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bankAccountService
      .query()
      .pipe(map((res: HttpResponse<IBankAccount[]>) => res.body ?? []))
      .pipe(
        map((bankAccounts: IBankAccount[]) =>
          this.bankAccountService.addBankAccountToCollectionIfMissing(
            bankAccounts,
            this.editForm.get('bankAccount')!.value,
            this.editForm.get('contraBankAccount')!.value
          )
        )
      )
      .subscribe((bankAccounts: IBankAccount[]) => (this.bankAccountsSharedCollection = bankAccounts));

    this.bankTransactionService
      .query()
      .pipe(map((res: HttpResponse<IBankTransaction[]>) => res.body ?? []))
      .pipe(
        map((bankTransactions: IBankTransaction[]) =>
          this.bankTransactionService.addBankTransactionToCollectionIfMissing(
            bankTransactions,
            ...(this.editForm.get('lefts')!.value ?? [])
          )
        )
      )
      .subscribe((bankTransactions: IBankTransaction[]) => (this.bankTransactionsSharedCollection = bankTransactions));

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

  protected createFromForm(): IBankTransaction {
    return {
      ...new BankTransaction(),
      id: this.editForm.get(['id'])!.value,
      bookingDate: this.editForm.get(['bookingDate'])!.value
        ? dayjs(this.editForm.get(['bookingDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      valueDate: this.editForm.get(['valueDate'])!.value ? dayjs(this.editForm.get(['valueDate'])!.value, DATE_TIME_FORMAT) : undefined,
      value: this.editForm.get(['value'])!.value,
      balanceAfter: this.editForm.get(['balanceAfter'])!.value,
      type: this.editForm.get(['type'])!.value,
      description: this.editForm.get(['description'])!.value,
      customerRef: this.editForm.get(['customerRef'])!.value,
      gvCode: this.editForm.get(['gvCode'])!.value,
      endToEnd: this.editForm.get(['endToEnd'])!.value,
      primanota: this.editForm.get(['primanota'])!.value,
      creditor: this.editForm.get(['creditor'])!.value,
      mandate: this.editForm.get(['mandate'])!.value,
      bankAccount: this.editForm.get(['bankAccount'])!.value,
      contraBankAccount: this.editForm.get(['contraBankAccount'])!.value,
      lefts: this.editForm.get(['lefts'])!.value,
      transactionBook: this.editForm.get(['transactionBook'])!.value,
    };
  }
}
