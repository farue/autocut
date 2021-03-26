import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IInternetAccess, InternetAccess } from '../internet-access.model';
import { InternetAccessService } from '../service/internet-access.service';
import { INetworkSwitch } from 'app/entities/network-switch/network-switch.model';
import { NetworkSwitchService } from 'app/entities/network-switch/service/network-switch.service';

@Component({
  selector: 'jhi-internet-access-update',
  templateUrl: './internet-access-update.component.html',
})
export class InternetAccessUpdateComponent implements OnInit {
  isSaving = false;

  networkSwitchesSharedCollection: INetworkSwitch[] = [];

  editForm = this.fb.group({
    id: [],
    blocked: [null, [Validators.required]],
    ip1: [null, [Validators.required]],
    ip2: [null, [Validators.required]],
    switchInterface: [null, [Validators.required]],
    port: [null, [Validators.required, Validators.min(1)]],
    networkSwitch: [],
  });

  constructor(
    protected internetAccessService: InternetAccessService,
    protected networkSwitchService: NetworkSwitchService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ internetAccess }) => {
      this.updateForm(internetAccess);

      this.loadRelationshipsOptions();
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

  trackNetworkSwitchById(index: number, item: INetworkSwitch): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInternetAccess>>): void {
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

  protected updateForm(internetAccess: IInternetAccess): void {
    this.editForm.patchValue({
      id: internetAccess.id,
      blocked: internetAccess.blocked,
      ip1: internetAccess.ip1,
      ip2: internetAccess.ip2,
      switchInterface: internetAccess.switchInterface,
      port: internetAccess.port,
      networkSwitch: internetAccess.networkSwitch,
    });

    this.networkSwitchesSharedCollection = this.networkSwitchService.addNetworkSwitchToCollectionIfMissing(
      this.networkSwitchesSharedCollection,
      internetAccess.networkSwitch
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

  protected createFromForm(): IInternetAccess {
    return {
      ...new InternetAccess(),
      id: this.editForm.get(['id'])!.value,
      blocked: this.editForm.get(['blocked'])!.value,
      ip1: this.editForm.get(['ip1'])!.value,
      ip2: this.editForm.get(['ip2'])!.value,
      switchInterface: this.editForm.get(['switchInterface'])!.value,
      port: this.editForm.get(['port'])!.value,
      networkSwitch: this.editForm.get(['networkSwitch'])!.value,
    };
  }
}
