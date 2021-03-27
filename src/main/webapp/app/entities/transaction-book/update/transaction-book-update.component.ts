import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITransactionBook, TransactionBook } from '../transaction-book.model';
import { TransactionBookService } from '../service/transaction-book.service';

@Component({
  selector: 'jhi-transaction-book-update',
  templateUrl: './transaction-book-update.component.html',
})
export class TransactionBookUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    type: [null, [Validators.required]],
  });

  constructor(
    protected transactionBookService: TransactionBookService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transactionBook }) => {
      this.updateForm(transactionBook);
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransactionBook>>): void {
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

  protected updateForm(transactionBook: ITransactionBook): void {
    this.editForm.patchValue({
      id: transactionBook.id,
      name: transactionBook.name,
      type: transactionBook.type,
    });
  }

  protected createFromForm(): ITransactionBook {
    return {
      ...new TransactionBook(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
