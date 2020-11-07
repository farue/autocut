import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { BankTransaction, IBankTransaction } from 'app/shared/model/bank-transaction.model';
import { BankTransactionService } from './bank-transaction.service';
import { IBankAccount } from 'app/shared/model/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/bank-account.service';
import { ITransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/transaction-book.service';

type SelectableEntity = IBankAccount | IBankTransaction | ITransactionBook;

@Component({
  selector: 'jhi-bank-transaction-update',
  templateUrl: './bank-transaction-update.component.html',
})
export class BankTransactionUpdateComponent implements OnInit {
  isSaving = false;
  bankaccounts: IBankAccount[] = [];
  banktransactions: IBankTransaction[] = [];
  transactionbooks: ITransactionBook[] = [];

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
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bankTransaction }) => {
      if (!bankTransaction.id) {
        const today = moment().startOf('day');
        bankTransaction.bookingDate = today;
        bankTransaction.valueDate = today;
      }

      this.updateForm(bankTransaction);

      this.bankAccountService.query().subscribe((res: HttpResponse<IBankAccount[]>) => (this.bankaccounts = res.body || []));

      this.bankTransactionService.query().subscribe((res: HttpResponse<IBankTransaction[]>) => (this.banktransactions = res.body || []));

      this.transactionBookService.query().subscribe((res: HttpResponse<ITransactionBook[]>) => (this.transactionbooks = res.body || []));
    });
  }

  updateForm(bankTransaction: IBankTransaction): void {
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

  private createFromForm(): IBankTransaction {
    return {
      ...new BankTransaction(),
      id: this.editForm.get(['id'])!.value,
      bookingDate: this.editForm.get(['bookingDate'])!.value
        ? moment(this.editForm.get(['bookingDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      valueDate: this.editForm.get(['valueDate'])!.value ? moment(this.editForm.get(['valueDate'])!.value, DATE_TIME_FORMAT) : undefined,
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBankTransaction>>): void {
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

  getSelected(selectedVals: IBankTransaction[], option: IBankTransaction): IBankTransaction {
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
