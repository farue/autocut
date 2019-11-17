import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { IInternetAccess, InternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';
import { IPort } from 'app/shared/model/port.model';
import { PortService } from 'app/entities/port/port.service';
import { IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from 'app/entities/apartment/apartment.service';

@Component({
  selector: 'jhi-internet-access-update',
  templateUrl: './internet-access-update.component.html'
})
export class InternetAccessUpdateComponent implements OnInit {
  isSaving: boolean;

  ports: IPort[];

  apartments: IApartment[];

  editForm = this.fb.group({
    id: [],
    blocked: [null, [Validators.required]],
    ip1: [null, [Validators.required]],
    ip2: [null, [Validators.required]],
    port: [null, Validators.required]
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected internetAccessService: InternetAccessService,
    protected portService: PortService,
    protected apartmentService: ApartmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ internetAccess }) => {
      this.updateForm(internetAccess);
    });
    this.portService.query({ filter: 'internetaccess-is-null' }).subscribe(
      (res: HttpResponse<IPort[]>) => {
        if (!this.editForm.get('port').value || !this.editForm.get('port').value.id) {
          this.ports = res.body;
        } else {
          this.portService
            .find(this.editForm.get('port').value.id)
            .subscribe(
              (subRes: HttpResponse<IPort>) => (this.ports = [subRes.body].concat(res.body)),
              (subRes: HttpErrorResponse) => this.onError(subRes.message)
            );
        }
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
    this.apartmentService
      .query()
      .subscribe((res: HttpResponse<IApartment[]>) => (this.apartments = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(internetAccess: IInternetAccess) {
    this.editForm.patchValue({
      id: internetAccess.id,
      blocked: internetAccess.blocked,
      ip1: internetAccess.ip1,
      ip2: internetAccess.ip2,
      port: internetAccess.port
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      blocked: this.editForm.get(['blocked']).value,
      ip1: this.editForm.get(['ip1']).value,
      ip2: this.editForm.get(['ip2']).value,
      port: this.editForm.get(['port']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInternetAccess>>) {
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

  trackPortById(index: number, item: IPort) {
    return item.id;
  }

  trackApartmentById(index: number, item: IApartment) {
    return item.id;
  }
}
