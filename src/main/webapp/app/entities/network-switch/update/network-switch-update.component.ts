import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { INetworkSwitch, NetworkSwitch } from '../network-switch.model';
import { NetworkSwitchService } from '../service/network-switch.service';

@Component({
  selector: 'jhi-network-switch-update',
  templateUrl: './network-switch-update.component.html',
})
export class NetworkSwitchUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    interfaceName: [null, [Validators.required]],
    sshHost: [null, [Validators.required]],
  });

  constructor(protected networkSwitchService: NetworkSwitchService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => {
      this.updateForm(networkSwitch);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const networkSwitch = this.createFromForm();
    if (networkSwitch.id !== undefined) {
      this.subscribeToSaveResponse(this.networkSwitchService.update(networkSwitch));
    } else {
      this.subscribeToSaveResponse(this.networkSwitchService.create(networkSwitch));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INetworkSwitch>>): void {
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

  protected updateForm(networkSwitch: INetworkSwitch): void {
    this.editForm.patchValue({
      id: networkSwitch.id,
      interfaceName: networkSwitch.interfaceName,
      sshHost: networkSwitch.sshHost,
    });
  }

  protected createFromForm(): INetworkSwitch {
    return {
      ...new NetworkSwitch(),
      id: this.editForm.get(['id'])!.value,
      interfaceName: this.editForm.get(['interfaceName'])!.value,
      sshHost: this.editForm.get(['sshHost'])!.value,
    };
  }
}
