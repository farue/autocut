import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ILaundryMachine, LaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineService } from './laundry-machine.service';

@Component({
  selector: 'jhi-laundry-machine-update',
  templateUrl: './laundry-machine-update.component.html'
})
export class LaundryMachineUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    type: [null, [Validators.required]],
    enabled: [null, [Validators.required]]
  });

  constructor(protected laundryMachineService: LaundryMachineService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryMachine }) => {
      this.updateForm(laundryMachine);
    });
  }

  updateForm(laundryMachine: ILaundryMachine): void {
    this.editForm.patchValue({
      id: laundryMachine.id,
      name: laundryMachine.name,
      type: laundryMachine.type,
      enabled: laundryMachine.enabled
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const laundryMachine = this.createFromForm();
    if (laundryMachine.id !== undefined) {
      this.subscribeToSaveResponse(this.laundryMachineService.update(laundryMachine));
    } else {
      this.subscribeToSaveResponse(this.laundryMachineService.create(laundryMachine));
    }
  }

  private createFromForm(): ILaundryMachine {
    return {
      ...new LaundryMachine(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      type: this.editForm.get(['type'])!.value,
      enabled: this.editForm.get(['enabled'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILaundryMachine>>): void {
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
}
