import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { ITransaction, Transaction } from 'app/shared/model/transaction.model';
import { TransactionService } from './transaction.service';
import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from 'app/entities/lease/lease.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

type SelectableEntity = ILease | ITenant;

@Component({
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html',
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;
  leases: ILease[] = [];
  tenants: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    kind: [null, [Validators.required]],
    bookingDate: [null, [Validators.required]],
    valueDate: [null, [Validators.required]],
    value: [null, [Validators.required]],
    balanceAfter: [],
    description: [],
    issuer: [null, [Validators.required]],
    recipient: [],
    lease: [],
    tenant: [],
  });

  constructor(
    protected transactionService: TransactionService,
    protected leaseService: LeaseService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      if (!transaction.id) {
        const today = moment().startOf('day');
        transaction.bookingDate = today;
        transaction.valueDate = today;
      }

      this.updateForm(transaction);

      this.leaseService.query().subscribe((res: HttpResponse<ILease[]>) => (this.leases = res.body || []));

      this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));
    });
  }

  updateForm(transaction: ITransaction): void {
    this.editForm.patchValue({
      id: transaction.id,
      kind: transaction.kind,
      bookingDate: transaction.bookingDate ? transaction.bookingDate.format(DATE_TIME_FORMAT) : null,
      valueDate: transaction.valueDate ? transaction.valueDate.format(DATE_TIME_FORMAT) : null,
      value: transaction.value,
      balanceAfter: transaction.balanceAfter,
      description: transaction.description,
      issuer: transaction.issuer,
      recipient: transaction.recipient,
      lease: transaction.lease,
      tenant: transaction.tenant,
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

  private createFromForm(): ITransaction {
    return {
      ...new Transaction(),
      id: this.editForm.get(['id'])!.value,
      kind: this.editForm.get(['kind'])!.value,
      bookingDate: this.editForm.get(['bookingDate'])!.value
        ? moment(this.editForm.get(['bookingDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      valueDate: this.editForm.get(['valueDate'])!.value ? moment(this.editForm.get(['valueDate'])!.value, DATE_TIME_FORMAT) : undefined,
      value: this.editForm.get(['value'])!.value,
      balanceAfter: this.editForm.get(['balanceAfter'])!.value,
      description: this.editForm.get(['description'])!.value,
      issuer: this.editForm.get(['issuer'])!.value,
      recipient: this.editForm.get(['recipient'])!.value,
      lease: this.editForm.get(['lease'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
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
}
