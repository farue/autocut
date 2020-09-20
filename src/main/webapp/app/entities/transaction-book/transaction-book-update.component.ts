import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ITransactionBook, TransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionBookService } from './transaction-book.service';

@Component({
  selector: 'jhi-transaction-book-update',
  templateUrl: './transaction-book-update.component.html',
})
export class TransactionBookUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
  });

  constructor(
    protected transactionBookService: TransactionBookService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transactionBook }) => {
      this.updateForm(transactionBook);
    });
  }

  updateForm(transactionBook: ITransactionBook): void {
    this.editForm.patchValue({
      id: transactionBook.id,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transactionBook = this.createFromForm();
    if (transactionBook.id !== undefined) {
      this.subscribeToSaveResponse(this.transactionBookService.update(transactionBook));
    } else {
      this.subscribeToSaveResponse(this.transactionBookService.create(transactionBook));
    }
  }

  private createFromForm(): ITransactionBook {
    return {
      ...new TransactionBook(),
      id: this.editForm.get(['id'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransactionBook>>): void {
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
