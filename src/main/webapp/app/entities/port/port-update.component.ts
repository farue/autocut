import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { IPort, Port } from 'app/shared/model/port.model';
import { PortService } from './port.service';
import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from 'app/entities/internet-access/internet-access.service';
import { INetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from 'app/entities/network-switch/network-switch.service';

@Component({
  selector: 'jhi-port-update',
  templateUrl: './port-update.component.html'
})
export class PortUpdateComponent implements OnInit {
  isSaving: boolean;

  internetaccesses: IInternetAccess[];

  networkswitches: INetworkSwitch[];

  editForm = this.fb.group({
    id: [],
    number: [null, [Validators.required, Validators.min(1)]],
    networkSwitch: [null, Validators.required]
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected portService: PortService,
    protected internetAccessService: InternetAccessService,
    protected networkSwitchService: NetworkSwitchService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ port }) => {
      this.updateForm(port);
    });
    this.internetAccessService
      .query()
      .subscribe(
        (res: HttpResponse<IInternetAccess[]>) => (this.internetaccesses = res.body),
        (res: HttpErrorResponse) => this.onError(res.message)
      );
    this.networkSwitchService
      .query()
      .subscribe(
        (res: HttpResponse<INetworkSwitch[]>) => (this.networkswitches = res.body),
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  updateForm(port: IPort) {
    this.editForm.patchValue({
      id: port.id,
      number: port.number,
      networkSwitch: port.networkSwitch
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      number: this.editForm.get(['number']).value,
      networkSwitch: this.editForm.get(['networkSwitch']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPort>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackInternetAccessById(index: number, item: IInternetAccess) {
    return item.id;
  }

  trackNetworkSwitchById(index: number, item: INetworkSwitch) {
    return item.id;
  }
}
