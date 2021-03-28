import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { INetworkSwitchStatus, NetworkSwitchStatus } from '../network-switch-status.model';
import { NetworkSwitchStatusService } from '../service/network-switch-status.service';
import { INetworkSwitch } from 'app/entities/network-switch/network-switch.model';
import { NetworkSwitchService } from 'app/entities/network-switch/service/network-switch.service';

@Component({
  selector: 'jhi-network-switch-status-update',
  templateUrl: './network-switch-status-update.component.html',
})
export class NetworkSwitchStatusUpdateComponent implements OnInit {
  isSaving = false;

  networkSwitchesSharedCollection: INetworkSwitch[] = [];

  editForm = this.fb.group({
    id: [],
    port: [null, [Validators.required]],
    name: [],
    status: [],
    vlan: [],
    speed: [],
    type: [],
    timestamp: [null, [Validators.required]],
    networkSwitch: [],
  });

  constructor(
    protected networkSwitchStatusService: NetworkSwitchStatusService,
    protected networkSwitchService: NetworkSwitchService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitchStatus }) => {
      if (networkSwitchStatus.id === undefined) {
        const today = dayjs().startOf('day');
        networkSwitchStatus.timestamp = today;
      }

      this.updateForm(networkSwitchStatus);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const networkSwitchStatus = this.createFromForm();
    if (networkSwitchStatus.id !== undefined) {
      this.subscribeToSaveResponse(this.networkSwitchStatusService.update(networkSwitchStatus));
    } else {
      this.subscribeToSaveResponse(this.networkSwitchStatusService.create(networkSwitchStatus));
    }
  }

  trackNetworkSwitchById(index: number, item: INetworkSwitch): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INetworkSwitchStatus>>): void {
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

  protected updateForm(networkSwitchStatus: INetworkSwitchStatus): void {
    this.editForm.patchValue({
      id: networkSwitchStatus.id,
      port: networkSwitchStatus.port,
      name: networkSwitchStatus.name,
      status: networkSwitchStatus.status,
      vlan: networkSwitchStatus.vlan,
      speed: networkSwitchStatus.speed,
      type: networkSwitchStatus.type,
      timestamp: networkSwitchStatus.timestamp ? networkSwitchStatus.timestamp.format(DATE_TIME_FORMAT) : null,
      networkSwitch: networkSwitchStatus.networkSwitch,
    });

    this.networkSwitchesSharedCollection = this.networkSwitchService.addNetworkSwitchToCollectionIfMissing(
      this.networkSwitchesSharedCollection,
      networkSwitchStatus.networkSwitch
    );
  }

  protected loadRelationshipsOptions(): void {
    this.networkSwitchService
      .query()
      .pipe(map((res: HttpResponse<INetworkSwitch[]>) => res.body ?? []))
      .pipe(
        map((networkSwitches: INetworkSwitch[]) =>
          this.networkSwitchService.addNetworkSwitchToCollectionIfMissing(networkSwitches, this.editForm.get('networkSwitch')!.value)
        )
      )
      .subscribe((networkSwitches: INetworkSwitch[]) => (this.networkSwitchesSharedCollection = networkSwitches));
  }

  protected createFromForm(): INetworkSwitchStatus {
    return {
      ...new NetworkSwitchStatus(),
      id: this.editForm.get(['id'])!.value,
      port: this.editForm.get(['port'])!.value,
      name: this.editForm.get(['name'])!.value,
      status: this.editForm.get(['status'])!.value,
      vlan: this.editForm.get(['vlan'])!.value,
      speed: this.editForm.get(['speed'])!.value,
      type: this.editForm.get(['type'])!.value,
      timestamp: this.editForm.get(['timestamp'])!.value ? dayjs(this.editForm.get(['timestamp'])!.value, DATE_TIME_FORMAT) : undefined,
      networkSwitch: this.editForm.get(['networkSwitch'])!.value,
    };
  }
}
