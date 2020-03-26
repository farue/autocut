import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { ICoin, Coin } from 'app/shared/model/coin.model';
import { CoinService } from './coin.service';
import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from 'app/entities/lease/lease.service';

@Component({
  selector: 'jhi-coin-update',
  templateUrl: './coin-update.component.html'
})
export class CoinUpdateComponent implements OnInit {
  isSaving = false;

  leases: ILease[] = [];

  editForm = this.fb.group({
    id: [],
    token: [null, [Validators.required]],
    datePurchase: [],
    dateRedeem: [],
    tenant: []
  });

  constructor(
    protected coinService: CoinService,
    protected leaseService: LeaseService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coin }) => {
      this.updateForm(coin);

      this.leaseService
        .query()
        .pipe(
          map((res: HttpResponse<ILease[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ILease[]) => (this.leases = resBody));
    });
  }

  updateForm(coin: ICoin): void {
    this.editForm.patchValue({
      id: coin.id,
      token: coin.token,
      datePurchase: coin.datePurchase != null ? coin.datePurchase.format(DATE_TIME_FORMAT) : null,
      dateRedeem: coin.dateRedeem != null ? coin.dateRedeem.format(DATE_TIME_FORMAT) : null,
      tenant: coin.tenant
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const coin = this.createFromForm();
    if (coin.id !== undefined) {
      this.subscribeToSaveResponse(this.coinService.update(coin));
    } else {
      this.subscribeToSaveResponse(this.coinService.create(coin));
    }
  }

  private createFromForm(): ICoin {
    return {
      ...new Coin(),
      id: this.editForm.get(['id'])!.value,
      token: this.editForm.get(['token'])!.value,
      datePurchase:
        this.editForm.get(['datePurchase'])!.value != null
          ? moment(this.editForm.get(['datePurchase'])!.value, DATE_TIME_FORMAT)
          : undefined,
      dateRedeem:
        this.editForm.get(['dateRedeem'])!.value != null ? moment(this.editForm.get(['dateRedeem'])!.value, DATE_TIME_FORMAT) : undefined,
      tenant: this.editForm.get(['tenant'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICoin>>): void {
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

  trackById(index: number, item: ILease): any {
    return item.id;
  }
}
