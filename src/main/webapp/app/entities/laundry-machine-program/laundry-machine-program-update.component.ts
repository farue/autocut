import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ILaundryMachineProgram, LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { LaundryMachineProgramService } from './laundry-machine-program.service';
import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineService } from 'app/entities/laundry-machine/laundry-machine.service';

@Component({
  selector: 'jhi-laundry-machine-program-update',
  templateUrl: './laundry-machine-program-update.component.html'
})
export class LaundryMachineProgramUpdateComponent implements OnInit {
  isSaving = false;

  laundrymachines: ILaundryMachine[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    time: [null, [Validators.required]],
    temperature: [],
    spin: [],
    preWash: [],
    protect: [],
    shortCycle: [],
    wrinkle: [],
    laundryMachine: []
  });

  constructor(
    protected laundryMachineProgramService: LaundryMachineProgramService,
    protected laundryMachineService: LaundryMachineService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryMachineProgram }) => {
      this.updateForm(laundryMachineProgram);

      this.laundryMachineService
        .query()
        .pipe(
          map((res: HttpResponse<ILaundryMachine[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ILaundryMachine[]) => (this.laundrymachines = resBody));
    });
  }

  updateForm(laundryMachineProgram: ILaundryMachineProgram): void {
    this.editForm.patchValue({
      id: laundryMachineProgram.id,
      name: laundryMachineProgram.name,
      time: laundryMachineProgram.time,
      temperature: laundryMachineProgram.temperature,
      spin: laundryMachineProgram.spin,
      preWash: laundryMachineProgram.preWash,
      protect: laundryMachineProgram.protect,
      shortCycle: laundryMachineProgram.shortCycle,
      wrinkle: laundryMachineProgram.wrinkle,
      laundryMachine: laundryMachineProgram.laundryMachine
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

  private createFromForm(): ILaundryMachineProgram {
    return {
      ...new LaundryMachineProgram(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      time: this.editForm.get(['time'])!.value,
      temperature: this.editForm.get(['temperature'])!.value,
      spin: this.editForm.get(['spin'])!.value,
      preWash: this.editForm.get(['preWash'])!.value,
      protect: this.editForm.get(['protect'])!.value,
      shortCycle: this.editForm.get(['shortCycle'])!.value,
      wrinkle: this.editForm.get(['wrinkle'])!.value,
      laundryMachine: this.editForm.get(['laundryMachine'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILaundryMachineProgram>>): void {
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

  trackById(index: number, item: ILaundryMachine): any {
    return item.id;
  }
}
