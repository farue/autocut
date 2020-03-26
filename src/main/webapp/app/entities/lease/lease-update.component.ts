import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { ILease, Lease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';
import { IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from 'app/entities/apartment/apartment.service';

@Component({
  selector: 'jhi-lease-update',
  templateUrl: './lease-update.component.html'
})
export class LeaseUpdateComponent implements OnInit {
  isSaving = false;

  apartments: IApartment[] = [];

  editForm = this.fb.group({
    id: [],
    nr: [null, [Validators.required]],
    start: [null, [Validators.required]],
    end: [],
    createdBy: [null, [Validators.required]],
    createdDate: [null, [Validators.required]],
    lastModifiedBy: [],
    lastModifiedDate: [],
    apartment: []
  });

  constructor(
    protected leaseService: LeaseService,
    protected apartmentService: ApartmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lease }) => {
      this.updateForm(lease);

      this.apartmentService
        .query()
        .pipe(
          map((res: HttpResponse<IApartment[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IApartment[]) => (this.apartments = resBody));
    });
  }

  updateForm(lease: ILease): void {
    this.editForm.patchValue({
      id: lease.id,
      nr: lease.nr,
      start: lease.start != null ? lease.start.format(DATE_TIME_FORMAT) : null,
      end: lease.end != null ? lease.end.format(DATE_TIME_FORMAT) : null,
      createdBy: lease.createdBy,
      createdDate: lease.createdDate != null ? lease.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: lease.lastModifiedBy,
      lastModifiedDate: lease.lastModifiedDate != null ? lease.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      apartment: lease.apartment
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
      start: this.editForm.get(['start'])!.value != null ? moment(this.editForm.get(['start'])!.value, DATE_TIME_FORMAT) : undefined,
      end: this.editForm.get(['end'])!.value != null ? moment(this.editForm.get(['end'])!.value, DATE_TIME_FORMAT) : undefined,
      createdBy: this.editForm.get(['createdBy'])!.value,
      createdDate:
        this.editForm.get(['createdDate'])!.value != null ? moment(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT) : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy'])!.value,
      lastModifiedDate:
        this.editForm.get(['lastModifiedDate'])!.value != null
          ? moment(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
          : undefined,
      apartment: this.editForm.get(['apartment'])!.value
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

  trackById(index: number, item: IApartment): any {
    return item.id;
  }
}
