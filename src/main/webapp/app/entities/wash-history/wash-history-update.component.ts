import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
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
  templateUrl: './wash-history-update.component.html',
})
export class WashHistoryUpdateComponent implements OnInit {
  isSaving = false;
  tenants: ITenant[] = [];
  laundrymachines: ILaundryMachine[] = [];
  laundrymachineprograms: ILaundryMachineProgram[] = [];

  editForm = this.fb.group({
    id: [],
    usingDate: [],
    reservationDate: [],
    lastModifiedDate: [],
    status: [],
    reservationTenant: [],
    usingTenant: [],
    machine: [],
    program: [],
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
      if (!washHistory.id) {
        const today = moment().startOf('day');
        washHistory.usingDate = today;
        washHistory.reservationDate = today;
        washHistory.lastModifiedDate = today;
      }

      this.updateForm(washHistory);

      this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));

      this.laundryMachineService.query().subscribe((res: HttpResponse<ILaundryMachine[]>) => (this.laundrymachines = res.body || []));

      this.laundryMachineProgramService
        .query()
        .subscribe((res: HttpResponse<ILaundryMachineProgram[]>) => (this.laundrymachineprograms = res.body || []));
    });
  }

  updateForm(washHistory: IWashHistory): void {
    this.editForm.patchValue({
      id: washHistory.id,
      usingDate: washHistory.usingDate ? washHistory.usingDate.format(DATE_TIME_FORMAT) : null,
      reservationDate: washHistory.reservationDate ? washHistory.reservationDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedDate: washHistory.lastModifiedDate ? washHistory.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      status: washHistory.status,
      reservationTenant: washHistory.reservationTenant,
      usingTenant: washHistory.usingTenant,
      machine: washHistory.machine,
      program: washHistory.program,
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
      usingDate: this.editForm.get(['usingDate'])!.value ? moment(this.editForm.get(['usingDate'])!.value, DATE_TIME_FORMAT) : undefined,
      reservationDate: this.editForm.get(['reservationDate'])!.value
        ? moment(this.editForm.get(['reservationDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      lastModifiedDate: this.editForm.get(['lastModifiedDate'])!.value
        ? moment(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      status: this.editForm.get(['status'])!.value,
      reservationTenant: this.editForm.get(['reservationTenant'])!.value,
      usingTenant: this.editForm.get(['usingTenant'])!.value,
      machine: this.editForm.get(['machine'])!.value,
      program: this.editForm.get(['program'])!.value,
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
