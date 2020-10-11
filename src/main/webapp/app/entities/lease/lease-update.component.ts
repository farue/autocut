import { Component, ElementRef, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiEventManager, JhiEventWithContent, JhiFileLoadError } from 'ng-jhipster';

import { ILease, Lease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { ITransactionBook } from 'app/shared/model/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/transaction-book.service';
import { IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from 'app/entities/apartment/apartment.service';

type SelectableEntity = ITransactionBook | IApartment;

@Component({
  selector: 'jhi-lease-update',
  templateUrl: './lease-update.component.html',
})
export class LeaseUpdateComponent implements OnInit {
  isSaving = false;
  transactionbooks: ITransactionBook[] = [];
  apartments: IApartment[] = [];

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
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected leaseService: LeaseService,
    protected transactionBookService: TransactionBookService,
    protected apartmentService: ApartmentService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lease }) => {
      if (!lease.id) {
        const today = moment().startOf('day');
        lease.start = today;
        lease.end = today;
      }

      this.updateForm(lease);

      this.transactionBookService.query().subscribe((res: HttpResponse<ITransactionBook[]>) => (this.transactionbooks = res.body || []));

      this.apartmentService.query().subscribe((res: HttpResponse<IApartment[]>) => (this.apartments = res.body || []));
    });
  }

  updateForm(lease: ILease): void {
    this.editForm.patchValue({
      id: lease.id,
      nr: lease.nr,
      start: lease.start ? lease.start.format(DATE_TIME_FORMAT) : null,
      end: lease.end ? lease.end.format(DATE_TIME_FORMAT) : null,
      blocked: lease.blocked,
      pictureContract: lease.pictureContract,
      pictureContractContentType: lease.pictureContractContentType,
      transactionBooks: lease.transactionBooks,
      apartment: lease.apartment,
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: any, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('autocutApp.error', { ...err, key: 'error.file.' + err.key })
      );
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (this.elementRef && idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
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

  private createFromForm(): ILease {
    return {
      ...new Lease(),
      id: this.editForm.get(['id'])!.value,
      nr: this.editForm.get(['nr'])!.value,
      start: this.editForm.get(['start'])!.value ? moment(this.editForm.get(['start'])!.value, DATE_TIME_FORMAT) : undefined,
      end: this.editForm.get(['end'])!.value ? moment(this.editForm.get(['end'])!.value, DATE_TIME_FORMAT) : undefined,
      blocked: this.editForm.get(['blocked'])!.value,
      pictureContractContentType: this.editForm.get(['pictureContractContentType'])!.value,
      pictureContract: this.editForm.get(['pictureContract'])!.value,
      transactionBooks: this.editForm.get(['transactionBooks'])!.value,
      apartment: this.editForm.get(['apartment'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILease>>): void {
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

  getSelected(selectedVals: ITransactionBook[], option: ITransactionBook): ITransactionBook {
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
