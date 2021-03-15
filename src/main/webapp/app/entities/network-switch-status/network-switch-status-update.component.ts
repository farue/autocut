import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { INetworkSwitchStatus, NetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';
import { NetworkSwitchStatusService } from './network-switch-status.service';
import { INetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from 'app/entities/network-switch/network-switch.service';

@Component({
  selector: 'jhi-network-switch-status-update',
  templateUrl: './network-switch-status-update.component.html',
})
export class NetworkSwitchStatusUpdateComponent implements OnInit {
  isSaving = false;
  networkswitches: INetworkSwitch[] = [];

  editForm = this.fb.group({
    id: [],
    port: [],
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
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitchStatus }) => {
      if (!networkSwitchStatus.id) {
        const today = moment().startOf('day');
        networkSwitchStatus.timestamp = today;
      }

      this.updateForm(networkSwitchStatus);

      this.networkSwitchService.query().subscribe((res: HttpResponse<INetworkSwitch[]>) => (this.networkswitches = res.body || []));
    });
  }

  updateForm(networkSwitchStatus: INetworkSwitchStatus): void {
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

  private createFromForm(): INetworkSwitchStatus {
    return {
      ...new NetworkSwitchStatus(),
      id: this.editForm.get(['id'])!.value,
      port: this.editForm.get(['port'])!.value,
      name: this.editForm.get(['name'])!.value,
      status: this.editForm.get(['status'])!.value,
      vlan: this.editForm.get(['vlan'])!.value,
      speed: this.editForm.get(['speed'])!.value,
      type: this.editForm.get(['type'])!.value,
      timestamp: this.editForm.get(['timestamp'])!.value ? moment(this.editForm.get(['timestamp'])!.value, DATE_TIME_FORMAT) : undefined,
      networkSwitch: this.editForm.get(['networkSwitch'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INetworkSwitchStatus>>): void {
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

  trackById(index: number, item: INetworkSwitch): any {
    return item.id;
  }
}
