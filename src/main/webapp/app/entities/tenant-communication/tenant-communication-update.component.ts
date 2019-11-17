import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ITenantCommunication, TenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

@Component({
  selector: 'jhi-tenant-communication-update',
  templateUrl: './tenant-communication-update.component.html'
})
export class TenantCommunicationUpdateComponent implements OnInit {
  isSaving: boolean;

  tenants: ITenant[];

  editForm = this.fb.group({
    id: [],
    text: [null, [Validators.required]],
    date: [null, [Validators.required]],
    tenant: [null, Validators.required]
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected tenantCommunicationService: TenantCommunicationService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ tenantCommunication }) => {
      this.updateForm(tenantCommunication);
    });
    this.tenantService
      .query()
      .subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(tenantCommunication: ITenantCommunication) {
    this.editForm.patchValue({
      id: tenantCommunication.id,
      text: tenantCommunication.text,
      date: tenantCommunication.date != null ? tenantCommunication.date.format(DATE_TIME_FORMAT) : null,
      tenant: tenantCommunication.tenant
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file: File = event.target.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      // eslint-disable-next-line no-console
      () => console.log('blob added'), // success
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      text: this.editForm.get(['text']).value,
      date: this.editForm.get(['date']).value != null ? moment(this.editForm.get(['date']).value, DATE_TIME_FORMAT) : undefined,
      tenant: this.editForm.get(['tenant']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITenantCommunication>>) {
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

  trackTenantById(index: number, item: ITenant) {
    return item.id;
  }
}
