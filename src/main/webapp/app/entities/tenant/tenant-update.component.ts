import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { ITenant, Tenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from 'app/entities/lease/lease.service';

type SelectableEntity = IUser | ILease;

@Component({
  selector: 'jhi-tenant-update',
  templateUrl: './tenant-update.component.html'
})
export class TenantUpdateComponent implements OnInit {
  isSaving = false;

  users: IUser[] = [];

  leases: ILease[] = [];

  editForm = this.fb.group({
    id: [],
    firstName: [null, [Validators.required]],
    lastName: [null, [Validators.required]],
    email: [null, [Validators.required]],
    createdBy: [null, [Validators.required]],
    createdDate: [null, [Validators.required]],
    lastModifiedBy: [],
    lastModifiedDate: [],
    user: [],
    lease: []
  });

  constructor(
    protected tenantService: TenantService,
    protected userService: UserService,
    protected leaseService: LeaseService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenant }) => {
      this.updateForm(tenant);

      this.userService
        .query()
        .pipe(
          map((res: HttpResponse<IUser[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IUser[]) => (this.users = resBody));

      this.leaseService
        .query()
        .pipe(
          map((res: HttpResponse<ILease[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ILease[]) => (this.leases = resBody));
    });
  }

  updateForm(tenant: ITenant): void {
    this.editForm.patchValue({
      id: tenant.id,
      firstName: tenant.firstName,
      lastName: tenant.lastName,
      email: tenant.email,
      createdBy: tenant.createdBy,
      createdDate: tenant.createdDate != null ? tenant.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: tenant.lastModifiedBy,
      lastModifiedDate: tenant.lastModifiedDate != null ? tenant.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      user: tenant.user,
      lease: tenant.lease
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tenant = this.createFromForm();
    if (tenant.id !== undefined) {
      this.subscribeToSaveResponse(this.tenantService.update(tenant));
    } else {
      this.subscribeToSaveResponse(this.tenantService.create(tenant));
    }
  }

  private createFromForm(): ITenant {
    return {
      ...new Tenant(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      createdDate:
        this.editForm.get(['createdDate'])!.value != null ? moment(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT) : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy'])!.value,
      lastModifiedDate:
        this.editForm.get(['lastModifiedDate'])!.value != null
          ? moment(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
          : undefined,
      user: this.editForm.get(['user'])!.value,
      lease: this.editForm.get(['lease'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITenant>>): void {
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

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
