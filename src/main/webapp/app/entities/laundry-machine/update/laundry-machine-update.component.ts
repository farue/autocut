import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize} from 'rxjs/operators';

import {ILaundryMachine, LaundryMachine} from '../laundry-machine.model';
import {LaundryMachineService} from '../service/laundry-machine.service';
import {LaundryMachineType} from 'app/entities/enumerations/laundry-machine-type.model';

@Component({
  selector: 'jhi-laundry-machine-update',
  templateUrl: './laundry-machine-update.component.html',
})
export class LaundryMachineUpdateComponent implements OnInit {
  isSaving = false;
  laundryMachineTypeValues = Object.keys(LaundryMachineType);

  editForm = this.fb.group({
    id: [],
    identifier: [null, [Validators.required]],
    name: [null, [Validators.required]],
    type: [null, [Validators.required]],
    enabled: [null, [Validators.required]],
    positionX: [null, [Validators.required]],
    positionY: [null, [Validators.required]],
  });

  constructor(
    protected laundryMachineService: LaundryMachineService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryMachine }) => {
      this.updateForm(laundryMachine);
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILaundryMachine>>): void {
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

  protected updateForm(laundryMachine: ILaundryMachine): void {
    this.editForm.patchValue({
      id: laundryMachine.id,
      identifier: laundryMachine.identifier,
      name: laundryMachine.name,
      type: laundryMachine.type,
      enabled: laundryMachine.enabled,
      positionX: laundryMachine.positionX,
      positionY: laundryMachine.positionY,
    });
  }

  protected createFromForm(): ILaundryMachine {
    return {
      ...new LaundryMachine(),
      id: this.editForm.get(['id'])!.value,
      identifier: this.editForm.get(['identifier'])!.value,
      name: this.editForm.get(['name'])!.value,
      type: this.editForm.get(['type'])!.value,
      enabled: this.editForm.get(['enabled'])!.value,
      positionX: this.editForm.get(['positionX'])!.value,
      positionY: this.editForm.get(['positionY'])!.value,
    };
  }
}
