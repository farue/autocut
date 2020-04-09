import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IWashHistory, WashHistory } from 'app/shared/model/wash-history.model';
import { WashHistoryService } from './wash-history.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';
import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/laundry-machine.service';
import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { LaundryMachineProgramService } from 'app/entities/laundry-machine-program/laundry-machine-program.service';

type SelectableEntity = ITenant | ILaundryMachine | ILaundryMachineProgram;

@Component({
  selector: 'jhi-wash-history-update',
  templateUrl: './wash-history-update.component.html'
})
export class WashHistoryUpdateComponent implements OnInit {
  isSaving = false;

  tenants: ITenant[] = [];

  laundrymachines: ILaundryMachine[] = [];

  laundrymachineprograms: ILaundryMachineProgram[] = [];

  editForm = this.fb.group({
    id: [],
    date: [],
    reservation: [],
    tenant: [],
    machine: [],
    program: []
  });

  constructor(
    protected washHistoryService: WashHistoryService,
    protected tenantService: TenantService,
    protected laundryMachineService: LaundryMachineService,
    protected laundryMachineProgramService: LaundryMachineProgramService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ washHistory }) => {
      this.updateForm(washHistory);

      this.tenantService
        .query()
        .pipe(
          map((res: HttpResponse<ITenant[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ITenant[]) => (this.tenants = resBody));

      this.laundryMachineService
        .query()
        .pipe(
          map((res: HttpResponse<ILaundryMachine[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ILaundryMachine[]) => (this.laundrymachines = resBody));

      this.laundryMachineProgramService
        .query()
        .pipe(
          map((res: HttpResponse<ILaundryMachineProgram[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ILaundryMachineProgram[]) => (this.laundrymachineprograms = resBody));
    });
  }

  updateForm(washHistory: IWashHistory): void {
    this.editForm.patchValue({
      id: washHistory.id,
      date: washHistory.date != null ? washHistory.date.format(DATE_TIME_FORMAT) : null,
      reservation: washHistory.reservation != null ? washHistory.reservation.format(DATE_TIME_FORMAT) : null,
      tenant: washHistory.tenant,
      machine: washHistory.machine,
      program: washHistory.program
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const washHistory = this.createFromForm();
    if (washHistory.id !== undefined) {
      this.subscribeToSaveResponse(this.washHistoryService.update(washHistory));
    } else {
      this.subscribeToSaveResponse(this.washHistoryService.create(washHistory));
    }
  }

  private createFromForm(): IWashHistory {
    return {
      ...new WashHistory(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value != null ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      reservation:
        this.editForm.get(['reservation'])!.value != null ? moment(this.editForm.get(['reservation'])!.value, DATE_TIME_FORMAT) : undefined,
      tenant: this.editForm.get(['tenant'])!.value,
      machine: this.editForm.get(['machine'])!.value,
      program: this.editForm.get(['program'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWashHistory>>): void {
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
