import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { ILease, Lease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';
import { IPaymentAccount } from 'app/shared/model/payment-account.model';
import { PaymentAccountService } from 'app/entities/payment-account/payment-account.service';
import { IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from 'app/entities/apartment/apartment.service';

@Component({
  selector: 'jhi-lease-update',
  templateUrl: './lease-update.component.html'
})
export class LeaseUpdateComponent implements OnInit {
  isSaving: boolean;

  accounts: IPaymentAccount[];

  apartments: IApartment[];

  editForm = this.fb.group({
    id: [],
    start: [null, [Validators.required]],
    end: [],
    account: [null, Validators.required],
    apartment: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected leaseService: LeaseService,
    protected paymentAccountService: PaymentAccountService,
    protected apartmentService: ApartmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ lease }) => {
      this.updateForm(lease);
    });
    this.paymentAccountService.query({ filter: 'lease-is-null' }).subscribe(
      (res: HttpResponse<IPaymentAccount[]>) => {
        if (!this.editForm.get('account').value || !this.editForm.get('account').value.id) {
          this.accounts = res.body;
        } else {
          this.paymentAccountService
            .find(this.editForm.get('account').value.id)
            .subscribe(
              (subRes: HttpResponse<IPaymentAccount>) => (this.accounts = [subRes.body].concat(res.body)),
              (subRes: HttpErrorResponse) => this.onError(subRes.message)
            );
        }
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
    this.apartmentService
      .query()
      .subscribe((res: HttpResponse<IApartment[]>) => (this.apartments = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(lease: ILease) {
    this.editForm.patchValue({
      id: lease.id,
      start: lease.start != null ? lease.start.format(DATE_TIME_FORMAT) : null,
      end: lease.end != null ? lease.end.format(DATE_TIME_FORMAT) : null,
      account: lease.account,
      apartment: lease.apartment
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      start: this.editForm.get(['start']).value != null ? moment(this.editForm.get(['start']).value, DATE_TIME_FORMAT) : undefined,
      end: this.editForm.get(['end']).value != null ? moment(this.editForm.get(['end']).value, DATE_TIME_FORMAT) : undefined,
      account: this.editForm.get(['account']).value,
      apartment: this.editForm.get(['apartment']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILease>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackPaymentAccountById(index: number, item: IPaymentAccount) {
    return item.id;
  }

  trackApartmentById(index: number, item: IApartment) {
    return item.id;
  }
}
