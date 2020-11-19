import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IRegistrationItem, RegistrationItem } from 'app/shared/model/registration-item.model';
import { RegistrationItemService } from './registration-item.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

@Component({
  selector: 'jhi-registration-item-update',
  templateUrl: './registration-item-update.component.html',
})
export class RegistrationItemUpdateComponent implements OnInit {
  isSaving = false;
  tenants: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    item: [null, [Validators.required]],
    contentType: [null, [Validators.required]],
    content: [null, [Validators.required, Validators.maxLength(4000)]],
    tenant: [],
  });

  constructor(
    protected registrationItemService: RegistrationItemService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ registrationItem }) => {
      this.updateForm(registrationItem);

      this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));
    });
  }

  updateForm(registrationItem: IRegistrationItem): void {
    this.editForm.patchValue({
      id: registrationItem.id,
      item: registrationItem.item,
      contentType: registrationItem.contentType,
      content: registrationItem.content,
      tenant: registrationItem.tenant,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const registrationItem = this.createFromForm();
    if (registrationItem.id !== undefined) {
      this.subscribeToSaveResponse(this.registrationItemService.update(registrationItem));
    } else {
      this.subscribeToSaveResponse(this.registrationItemService.create(registrationItem));
    }
  }

  private createFromForm(): IRegistrationItem {
    return {
      ...new RegistrationItem(),
      id: this.editForm.get(['id'])!.value,
      item: this.editForm.get(['item'])!.value,
      contentType: this.editForm.get(['contentType'])!.value,
      content: this.editForm.get(['content'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRegistrationItem>>): void {
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
