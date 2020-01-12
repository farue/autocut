import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { INetworkSwitch, NetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';

@Component({
  selector: 'jhi-network-switch-update',
  templateUrl: './network-switch-update.component.html'
})
export class NetworkSwitchUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    switchInterface: [null, [Validators.required]]
  });

  constructor(protected networkSwitchService: NetworkSwitchService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => {
      this.updateForm(networkSwitch);
    });
  }

  updateForm(networkSwitch: INetworkSwitch): void {
    this.editForm.patchValue({
      id: networkSwitch.id,
      switchInterface: networkSwitch.switchInterface
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

  private createFromForm(): INetworkSwitch {
    return {
      ...new NetworkSwitch(),
      id: this.editForm.get(['id'])!.value,
      switchInterface: this.editForm.get(['switchInterface'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INetworkSwitch>>): void {
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
