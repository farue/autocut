import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILaundryMachineProgram, LaundryMachineProgram } from '../laundry-machine-program.model';
import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';
import { ILaundryProgram } from 'app/entities/laundry-program/laundry-program.model';
import { LaundryProgramService } from 'app/entities/laundry-program/service/laundry-program.service';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/service/laundry-machine.service';

@Component({
  selector: 'jhi-laundry-machine-program-update',
  templateUrl: './laundry-machine-program-update.component.html',
})
export class LaundryMachineProgramUpdateComponent implements OnInit {
  isSaving = false;

  laundryProgramsSharedCollection: ILaundryProgram[] = [];
  laundryMachinesSharedCollection: ILaundryMachine[] = [];

  editForm = this.fb.group({
    id: [],
    time: [null, [Validators.required]],
    program: [null, Validators.required],
    machine: [null, Validators.required],
  });

  constructor(
    protected laundryMachineProgramService: LaundryMachineProgramService,
    protected laundryProgramService: LaundryProgramService,
    protected laundryMachineService: LaundryMachineService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryMachineProgram }) => {
      this.updateForm(laundryMachineProgram);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const laundryMachineProgram = this.createFromForm();
    if (laundryMachineProgram.id !== undefined) {
      this.subscribeToSaveResponse(this.laundryMachineProgramService.update(laundryMachineProgram));
    } else {
      this.subscribeToSaveResponse(this.laundryMachineProgramService.create(laundryMachineProgram));
    }
  }

  trackLaundryProgramById(index: number, item: ILaundryProgram): number {
    return item.id!;
  }

  trackLaundryMachineById(index: number, item: ILaundryMachine): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILaundryMachineProgram>>): void {
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

  protected updateForm(laundryMachineProgram: ILaundryMachineProgram): void {
    this.editForm.patchValue({
      id: laundryMachineProgram.id,
      time: laundryMachineProgram.time,
      program: laundryMachineProgram.program,
      machine: laundryMachineProgram.machine,
    });

    this.laundryProgramsSharedCollection = this.laundryProgramService.addLaundryProgramToCollectionIfMissing(
      this.laundryProgramsSharedCollection,
      laundryMachineProgram.program
    );
    this.laundryMachinesSharedCollection = this.laundryMachineService.addLaundryMachineToCollectionIfMissing(
      this.laundryMachinesSharedCollection,
      laundryMachineProgram.machine
    );
  }

  protected loadRelationshipsOptions(): void {
    this.laundryProgramService
      .query()
      .pipe(map((res: HttpResponse<ILaundryProgram[]>) => res.body ?? []))
      .pipe(
        map((laundryPrograms: ILaundryProgram[]) =>
          this.laundryProgramService.addLaundryProgramToCollectionIfMissing(laundryPrograms, this.editForm.get('program')!.value)
        )
      )
      .subscribe((laundryPrograms: ILaundryProgram[]) => (this.laundryProgramsSharedCollection = laundryPrograms));

    this.laundryMachineService
      .query()
      .pipe(map((res: HttpResponse<ILaundryMachine[]>) => res.body ?? []))
      .pipe(
        map((laundryMachines: ILaundryMachine[]) =>
          this.laundryMachineService.addLaundryMachineToCollectionIfMissing(laundryMachines, this.editForm.get('machine')!.value)
        )
      )
      .subscribe((laundryMachines: ILaundryMachine[]) => (this.laundryMachinesSharedCollection = laundryMachines));
  }

  protected createFromForm(): ILaundryMachineProgram {
    return {
      ...new LaundryMachineProgram(),
      id: this.editForm.get(['id'])!.value,
      time: this.editForm.get(['time'])!.value,
      program: this.editForm.get(['program'])!.value,
      machine: this.editForm.get(['machine'])!.value,
    };
  }
}
