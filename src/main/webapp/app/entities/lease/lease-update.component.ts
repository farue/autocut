import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ILease, Lease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';
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
  startDp: any;
  endDp: any;

  editForm = this.fb.group({
    id: [],
    nr: [null, [Validators.required]],
    start: [null, [Validators.required]],
    end: [null, [Validators.required]],
    blocked: [],
    transactionBooks: [],
    apartment: [],
  });

  constructor(
    protected leaseService: LeaseService,
    protected transactionBookService: TransactionBookService,
    protected apartmentService: ApartmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lease }) => {
      this.updateForm(lease);

      this.transactionBookService.query().subscribe((res: HttpResponse<ITransactionBook[]>) => (this.transactionbooks = res.body || []));

      this.apartmentService.query().subscribe((res: HttpResponse<IApartment[]>) => (this.apartments = res.body || []));
    });
  }

  updateForm(lease: ILease): void {
    this.editForm.patchValue({
      id: lease.id,
      nr: lease.nr,
      start: lease.start,
      end: lease.end,
      blocked: lease.blocked,
      transactionBooks: lease.transactionBooks,
      apartment: lease.apartment,
    });
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
      start: this.editForm.get(['start'])!.value,
      end: this.editForm.get(['end'])!.value,
      blocked: this.editForm.get(['blocked'])!.value,
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
