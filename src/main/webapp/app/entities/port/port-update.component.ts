import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IPort, Port } from 'app/shared/model/port.model';
import { PortService } from './port.service';
import { INetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from 'app/entities/network-switch/network-switch.service';

@Component({
  selector: 'jhi-port-update',
  templateUrl: './port-update.component.html'
})
export class PortUpdateComponent implements OnInit {
  isSaving = false;

  networkswitches: INetworkSwitch[] = [];

  editForm = this.fb.group({
    id: [],
    number: [null, [Validators.required, Validators.min(1)]],
    networkSwitch: [null, Validators.required]
  });

  constructor(
    protected portService: PortService,
    protected networkSwitchService: NetworkSwitchService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ port }) => {
      this.updateForm(port);

      this.networkSwitchService
        .query()
        .pipe(
          map((res: HttpResponse<INetworkSwitch[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: INetworkSwitch[]) => (this.networkswitches = resBody));
    });
  }

  updateForm(port: IPort): void {
    this.editForm.patchValue({
      id: port.id,
      number: port.number,
      networkSwitch: port.networkSwitch
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const port = this.createFromForm();
    if (port.id !== undefined) {
      this.subscribeToSaveResponse(this.portService.update(port));
    } else {
      this.subscribeToSaveResponse(this.portService.create(port));
    }
  }

  private createFromForm(): IPort {
    return {
      ...new Port(),
      id: this.editForm.get(['id'])!.value,
      number: this.editForm.get(['number'])!.value,
      networkSwitch: this.editForm.get(['networkSwitch'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPort>>): void {
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
