import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IWashHistory, WashHistory } from '../wash-history.model';
import { WashHistoryService } from '../service/wash-history.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/service/laundry-machine.service';
import { ILaundryMachineProgram } from 'app/entities/laundry-machine-program/laundry-machine-program.model';
import { LaundryMachineProgramService } from 'app/entities/laundry-machine-program/service/laundry-machine-program.service';
import { WashHistoryStatus } from 'app/entities/enumerations/wash-history-status.model';

@Component({
  selector: 'jhi-wash-history-update',
  templateUrl: './wash-history-update.component.html',
})
export class WashHistoryUpdateComponent implements OnInit {
  isSaving = false;
  washHistoryStatusValues = Object.keys(WashHistoryStatus);

  tenantsSharedCollection: ITenant[] = [];
  laundryMachinesSharedCollection: ILaundryMachine[] = [];
  laundryMachineProgramsSharedCollection: ILaundryMachineProgram[] = [];

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
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ washHistory }) => {
      if (washHistory.id === undefined) {
        const today = dayjs().startOf('day');
        washHistory.usingDate = today;
        washHistory.reservationDate = today;
        washHistory.lastModifiedDate = today;
      }

      this.updateForm(washHistory);

      this.loadRelationshipsOptions();
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

  trackTenantById(index: number, item: ITenant): number {
    return item.id!;
  }

  trackLaundryMachineById(index: number, item: ILaundryMachine): number {
    return item.id!;
  }

  trackLaundryMachineProgramById(index: number, item: ILaundryMachineProgram): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWashHistory>>): void {
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

  protected updateForm(washHistory: IWashHistory): void {
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

    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing(
      this.tenantsSharedCollection,
      washHistory.reservationTenant,
      washHistory.usingTenant
    );
    this.laundryMachinesSharedCollection = this.laundryMachineService.addLaundryMachineToCollectionIfMissing(
      this.laundryMachinesSharedCollection,
      washHistory.machine
    );
    this.laundryMachineProgramsSharedCollection = this.laundryMachineProgramService.addLaundryMachineProgramToCollectionIfMissing(
      this.laundryMachineProgramsSharedCollection,
      washHistory.program
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(
        map((tenants: ITenant[]) =>
          this.tenantService.addTenantToCollectionIfMissing(
            tenants,
            this.editForm.get('reservationTenant')!.value,
            this.editForm.get('usingTenant')!.value
          )
        )
      )
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));

    this.laundryMachineService
      .query()
      .pipe(map((res: HttpResponse<ILaundryMachine[]>) => res.body ?? []))
      .pipe(
        map((laundryMachines: ILaundryMachine[]) =>
          this.laundryMachineService.addLaundryMachineToCollectionIfMissing(laundryMachines, this.editForm.get('machine')!.value)
        )
      )
      .subscribe((laundryMachines: ILaundryMachine[]) => (this.laundryMachinesSharedCollection = laundryMachines));

    this.laundryMachineProgramService
      .query()
      .pipe(map((res: HttpResponse<ILaundryMachineProgram[]>) => res.body ?? []))
      .pipe(
        map((laundryMachinePrograms: ILaundryMachineProgram[]) =>
          this.laundryMachineProgramService.addLaundryMachineProgramToCollectionIfMissing(
            laundryMachinePrograms,
            this.editForm.get('program')!.value
          )
        )
      )
      .subscribe(
        (laundryMachinePrograms: ILaundryMachineProgram[]) => (this.laundryMachineProgramsSharedCollection = laundryMachinePrograms)
      );
  }

  protected createFromForm(): IWashHistory {
    return {
      ...new WashHistory(),
      id: this.editForm.get(['id'])!.value,
      usingDate: this.editForm.get(['usingDate'])!.value ? dayjs(this.editForm.get(['usingDate'])!.value, DATE_TIME_FORMAT) : undefined,
      reservationDate: this.editForm.get(['reservationDate'])!.value
        ? dayjs(this.editForm.get(['reservationDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      lastModifiedDate: this.editForm.get(['lastModifiedDate'])!.value
        ? dayjs(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      status: this.editForm.get(['status'])!.value,
      reservationTenant: this.editForm.get(['reservationTenant'])!.value,
      usingTenant: this.editForm.get(['usingTenant'])!.value,
      machine: this.editForm.get(['machine'])!.value,
      program: this.editForm.get(['program'])!.value,
    };
  }
}
