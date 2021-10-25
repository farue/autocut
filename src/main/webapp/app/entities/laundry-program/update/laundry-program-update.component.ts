import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ILaundryProgram, LaundryProgram } from '../laundry-program.model';
import { LaundryProgramService } from '../service/laundry-program.service';

@Component({
  selector: 'jhi-laundry-program-update',
  templateUrl: './laundry-program-update.component.html',
})
export class LaundryProgramUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    subprogram: [],
    spin: [],
    preWash: [],
    protect: [],
  });

  constructor(
    protected laundryProgramService: LaundryProgramService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryProgram }) => {
      this.updateForm(laundryProgram);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const laundryProgram = this.createFromForm();
    if (laundryProgram.id !== undefined) {
      this.subscribeToSaveResponse(this.laundryProgramService.update(laundryProgram));
    } else {
      this.subscribeToSaveResponse(this.laundryProgramService.create(laundryProgram));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILaundryProgram>>): void {
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

  protected updateForm(laundryProgram: ILaundryProgram): void {
    this.editForm.patchValue({
      id: laundryProgram.id,
      name: laundryProgram.name,
      subprogram: laundryProgram.subprogram,
      spin: laundryProgram.spin,
      preWash: laundryProgram.preWash,
      protect: laundryProgram.protect,
    });
  }

  protected createFromForm(): ILaundryProgram {
    return {
      ...new LaundryProgram(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      subprogram: this.editForm.get(['subprogram'])!.value,
      spin: this.editForm.get(['spin'])!.value,
      preWash: this.editForm.get(['preWash'])!.value,
      protect: this.editForm.get(['protect'])!.value,
    };
  }
}
