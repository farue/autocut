import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IInternetAccess, InternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';

@Component({
  selector: 'jhi-internet-access-update',
  templateUrl: './internet-access-update.component.html'
})
export class InternetAccessUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    blocked: [null, [Validators.required]],
    ip1: [null, [Validators.required]],
    ip2: [null, [Validators.required]],
    switchInterface: [null, [Validators.required]],
    port: [null, [Validators.required, Validators.min(1)]]
  });

  constructor(protected internetAccessService: InternetAccessService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ internetAccess }) => {
      this.updateForm(internetAccess);
    });
  }

  updateForm(internetAccess: IInternetAccess): void {
    this.editForm.patchValue({
      id: internetAccess.id,
      blocked: internetAccess.blocked,
      ip1: internetAccess.ip1,
      ip2: internetAccess.ip2,
      switchInterface: internetAccess.switchInterface,
      port: internetAccess.port
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const internetAccess = this.createFromForm();
    if (internetAccess.id !== undefined) {
      this.subscribeToSaveResponse(this.internetAccessService.update(internetAccess));
    } else {
      this.subscribeToSaveResponse(this.internetAccessService.create(internetAccess));
    }
  }

  private createFromForm(): IInternetAccess {
    return {
      ...new InternetAccess(),
      id: this.editForm.get(['id'])!.value,
      blocked: this.editForm.get(['blocked'])!.value,
      ip1: this.editForm.get(['ip1'])!.value,
      ip2: this.editForm.get(['ip2'])!.value,
      switchInterface: this.editForm.get(['switchInterface'])!.value,
      port: this.editForm.get(['port'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInternetAccess>>): void {
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
