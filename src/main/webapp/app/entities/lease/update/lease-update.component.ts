import { Component, ElementRef, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILease, Lease } from '../lease.model';
import { LeaseService } from '../service/lease.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';
import { IApartment } from 'app/entities/apartment/apartment.model';
import { ApartmentService } from 'app/entities/apartment/service/apartment.service';

@Component({
  selector: 'jhi-lease-update',
  templateUrl: './lease-update.component.html',
})
export class LeaseUpdateComponent implements OnInit {
  isSaving = false;

  transactionBooksSharedCollection: ITransactionBook[] = [];
  apartmentsSharedCollection: IApartment[] = [];

  editForm = this.fb.group({
    id: [],
    nr: [null, [Validators.required]],
    start: [null, [Validators.required]],
    end: [null, [Validators.required]],
    blocked: [],
    pictureContract: [],
    pictureContractContentType: [],
    transactionBooks: [],
    apartment: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected leaseService: LeaseService,
    protected transactionBookService: TransactionBookService,
    protected apartmentService: ApartmentService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lease }) => {
      this.updateForm(lease);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('autocutApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lease = this.createFromForm();
    if (lease.id !== undefined) {
      this.subscribeToSaveResponse(this.leaseService.update(lease));
    } else {
      this.subscribeToSaveResponse(this.leaseService.create(lease));
    }
  }

  trackTransactionBookById(index: number, item: ITransactionBook): number {
    return item.id!;
  }

  trackApartmentById(index: number, item: IApartment): number {
    return item.id!;
  }

  getSelectedTransactionBook(option: ITransactionBook, selectedVals?: ITransactionBook[]): ITransactionBook {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILease>>): void {
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

  protected updateForm(lease: ILease): void {
    this.editForm.patchValue({
      id: lease.id,
      nr: lease.nr,
      start: lease.start,
      end: lease.end,
      blocked: lease.blocked,
      pictureContract: lease.pictureContract,
      pictureContractContentType: lease.pictureContractContentType,
      transactionBooks: lease.transactionBooks,
      apartment: lease.apartment,
    });

    this.transactionBooksSharedCollection = this.transactionBookService.addTransactionBookToCollectionIfMissing(
      this.transactionBooksSharedCollection,
      ...(lease.transactionBooks ?? [])
    );
    this.apartmentsSharedCollection = this.apartmentService.addApartmentToCollectionIfMissing(
      this.apartmentsSharedCollection,
      lease.apartment
    );
  }

  protected loadRelationshipsOptions(): void {
    this.transactionBookService
      .query()
      .pipe(map((res: HttpResponse<ITransactionBook[]>) => res.body ?? []))
      .pipe(
        map((transactionBooks: ITransactionBook[]) =>
          this.transactionBookService.addTransactionBookToCollectionIfMissing(
            transactionBooks,
            ...(this.editForm.get('transactionBooks')!.value ?? [])
          )
        )
      )
      .subscribe((transactionBooks: ITransactionBook[]) => (this.transactionBooksSharedCollection = transactionBooks));

    this.apartmentService
      .query()
      .pipe(map((res: HttpResponse<IApartment[]>) => res.body ?? []))
      .pipe(
        map((apartments: IApartment[]) =>
          this.apartmentService.addApartmentToCollectionIfMissing(apartments, this.editForm.get('apartment')!.value)
        )
      )
      .subscribe((apartments: IApartment[]) => (this.apartmentsSharedCollection = apartments));
  }

  protected createFromForm(): ILease {
    return {
      ...new Lease(),
      id: this.editForm.get(['id'])!.value,
      nr: this.editForm.get(['nr'])!.value,
      start: this.editForm.get(['start'])!.value,
      end: this.editForm.get(['end'])!.value,
      blocked: this.editForm.get(['blocked'])!.value,
      pictureContractContentType: this.editForm.get(['pictureContractContentType'])!.value,
      pictureContract: this.editForm.get(['pictureContract'])!.value,
      transactionBooks: this.editForm.get(['transactionBooks'])!.value,
      apartment: this.editForm.get(['apartment'])!.value,
    };
  }
}
