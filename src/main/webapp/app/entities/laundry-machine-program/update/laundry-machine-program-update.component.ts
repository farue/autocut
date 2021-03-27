import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILaundryMachineProgram, LaundryMachineProgram } from '../laundry-machine-program.model';
import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';
import { ILaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/service/laundry-machine.service';

@Component({
  selector: 'jhi-laundry-machine-program-update',
  templateUrl: './laundry-machine-program-update.component.html',
})
export class LaundryMachineProgramUpdateComponent implements OnInit {
  isSaving = false;

  laundryMachinesSharedCollection: ILaundryMachine[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    subprogram: [],
    time: [null, [Validators.required]],
    spin: [],
    preWash: [],
    protect: [],
    laundryMachine: [],
  });

  constructor(
    protected laundryMachineProgramService: LaundryMachineProgramService,
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

  trackLaundryMachineById(index: number, item: ILaundryMachine): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILaundryMachineProgram>>): void {
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

  protected updateForm(laundryMachineProgram: ILaundryMachineProgram): void {
    this.editForm.patchValue({
      id: laundryMachineProgram.id,
      name: laundryMachineProgram.name,
      subprogram: laundryMachineProgram.subprogram,
      time: laundryMachineProgram.time,
      spin: laundryMachineProgram.spin,
      preWash: laundryMachineProgram.preWash,
      protect: laundryMachineProgram.protect,
      laundryMachine: laundryMachineProgram.laundryMachine,
    });

    this.laundryMachinesSharedCollection = this.laundryMachineService.addLaundryMachineToCollectionIfMissing(
      this.laundryMachinesSharedCollection,
      laundryMachineProgram.laundryMachine
    );
  }

  protected loadRelationshipsOptions(): void {
    this.laundryMachineService
      .query()
      .pipe(map((res: HttpResponse<ILaundryMachine[]>) => res.body ?? []))
      .pipe(
        map((laundryMachines: ILaundryMachine[]) =>
          this.laundryMachineService.addLaundryMachineToCollectionIfMissing(laundryMachines, this.editForm.get('laundryMachine')!.value)
        )
      )
      .subscribe((laundryMachines: ILaundryMachine[]) => (this.laundryMachinesSharedCollection = laundryMachines));
  }

  protected createFromForm(): ILaundryMachineProgram {
    return {
      ...new LaundryMachineProgram(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      subprogram: this.editForm.get(['subprogram'])!.value,
      time: this.editForm.get(['time'])!.value,
      spin: this.editForm.get(['spin'])!.value,
      preWash: this.editForm.get(['preWash'])!.value,
      protect: this.editForm.get(['protect'])!.value,
      laundryMachine: this.editForm.get(['laundryMachine'])!.value,
    };
  }
}
