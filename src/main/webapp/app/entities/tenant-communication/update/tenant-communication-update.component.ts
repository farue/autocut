import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize, map} from 'rxjs/operators';

import * as dayjs from 'dayjs';
import {DATE_TIME_FORMAT} from 'app/config/input.constants';

import {ITenantCommunication, TenantCommunication} from '../tenant-communication.model';
import {TenantCommunicationService} from '../service/tenant-communication.service';
import {AlertError} from 'app/shared/alert/alert-error.model';
import {EventManager, EventWithContent} from 'app/core/util/event-manager.service';
import {DataUtils, FileLoadError} from 'app/core/util/data-util.service';
import {ITenant} from 'app/entities/tenant/tenant.model';
import {TenantService} from 'app/entities/tenant/service/tenant.service';

@Component({
  selector: 'jhi-tenant-communication-update',
  templateUrl: './tenant-communication-update.component.html',
})
export class TenantCommunicationUpdateComponent implements OnInit {
  isSaving = false;

  tenantsSharedCollection: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    subject: [null, [Validators.required, Validators.maxLength(80)]],
    text: [null, [Validators.required]],
    note: [],
    date: [null, [Validators.required]],
    tenant: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected tenantCommunicationService: TenantCommunicationService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantCommunication }) => {
      if (tenantCommunication.id === undefined) {
        const today = dayjs().startOf('day');
        tenantCommunication.date = today;
      }

      this.updateForm(tenantCommunication);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('autocutApp.error', { ...err, key: 'error.file.' + err.key })),
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

  trackTenantById(index: number, item: ITenant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITenantCommunication>>): void {
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

  protected updateForm(tenantCommunication: ITenantCommunication): void {
    this.editForm.patchValue({
      id: tenantCommunication.id,
      subject: tenantCommunication.subject,
      text: tenantCommunication.text,
      note: tenantCommunication.note,
      date: tenantCommunication.date ? tenantCommunication.date.format(DATE_TIME_FORMAT) : null,
      tenant: tenantCommunication.tenant,
    });

    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing(
      this.tenantsSharedCollection,
      tenantCommunication.tenant
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing(tenants, this.editForm.get('tenant')!.value)))
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));
  }

  protected createFromForm(): ITenantCommunication {
    return {
      ...new TenantCommunication(),
      id: this.editForm.get(['id'])!.value,
      subject: this.editForm.get(['subject'])!.value,
      text: this.editForm.get(['text'])!.value,
      note: this.editForm.get(['note'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      tenant: this.editForm.get(['tenant'])!.value,
    };
  }
}
