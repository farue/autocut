import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { INetworkSwitch, NetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

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
    sshPort: [null, [Validators.required, Validators.min(0), Validators.max(65535)]],
    sshKey: [null, [Validators.required]],
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected networkSwitchService: NetworkSwitchService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => {
      this.updateForm(networkSwitch);
    });
  }

  updateForm(networkSwitch: INetworkSwitch): void {
    this.editForm.patchValue({
      id: networkSwitch.id,
      interfaceName: networkSwitch.interfaceName,
      sshHost: networkSwitch.sshHost,
      sshPort: networkSwitch.sshPort,
      sshKey: networkSwitch.sshKey,
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: any, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('autocutApp.error', { ...err, key: 'error.file.' + err.key })
      );
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
      interfaceName: this.editForm.get(['interfaceName'])!.value,
      sshHost: this.editForm.get(['sshHost'])!.value,
      sshPort: this.editForm.get(['sshPort'])!.value,
      sshKey: this.editForm.get(['sshKey'])!.value,
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
