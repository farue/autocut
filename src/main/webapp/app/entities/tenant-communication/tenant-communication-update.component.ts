import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { ITenantCommunication, TenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

@Component({
  selector: 'jhi-tenant-communication-update',
  templateUrl: './tenant-communication-update.component.html'
})
export class TenantCommunicationUpdateComponent implements OnInit {
  isSaving = false;

  tenants: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    subject: [null, [Validators.required, Validators.maxLength(80)]],
    text: [null, [Validators.required]],
    note: [],
    date: [null, [Validators.required]],
    tenant: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected tenantCommunicationService: TenantCommunicationService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantCommunication }) => {
      this.updateForm(tenantCommunication);

      this.tenantService
        .query()
        .pipe(
          map((res: HttpResponse<ITenant[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ITenant[]) => (this.tenants = resBody));
    });
  }

  updateForm(tenantCommunication: ITenantCommunication): void {
    this.editForm.patchValue({
      id: tenantCommunication.id,
      subject: tenantCommunication.subject,
      text: tenantCommunication.text,
      note: tenantCommunication.note,
      date: tenantCommunication.date != null ? tenantCommunication.date.format(DATE_TIME_FORMAT) : null,
      tenant: tenantCommunication.tenant
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
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
    const tenantCommunication = this.createFromForm();
    if (tenantCommunication.id !== undefined) {
      this.subscribeToSaveResponse(this.tenantCommunicationService.update(tenantCommunication));
    } else {
      this.subscribeToSaveResponse(this.tenantCommunicationService.create(tenantCommunication));
    }
  }

  private createFromForm(): ITenantCommunication {
    return {
      ...new TenantCommunication(),
      id: this.editForm.get(['id'])!.value,
      subject: this.editForm.get(['subject'])!.value,
      text: this.editForm.get(['text'])!.value,
      note: this.editForm.get(['note'])!.value,
      date: this.editForm.get(['date'])!.value != null ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      tenant: this.editForm.get(['tenant'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITenantCommunication>>): void {
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

  trackById(index: number, item: ITenant): any {
    return item.id;
  }
}
