import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ITenant, Tenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from 'app/entities/lease/lease.service';

type SelectableEntity = IUser | ILease;

@Component({
  selector: 'jhi-tenant-update',
  templateUrl: './tenant-update.component.html',
})
export class TenantUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  leases: ILease[] = [];

  editForm = this.fb.group({
    id: [],
    firstName: [null, [Validators.required]],
    lastName: [null, [Validators.required]],
    verified: [],
    user: [],
    lease: [],
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

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

      this.leaseService.query().subscribe((res: HttpResponse<ILease[]>) => (this.leases = res.body || []));
    });
  }

  updateForm(tenant: ITenant): void {
    this.editForm.patchValue({
      id: tenant.id,
      firstName: tenant.firstName,
      lastName: tenant.lastName,
      verified: tenant.verified,
      user: tenant.user,
      lease: tenant.lease,
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
      verified: this.editForm.get(['verified'])!.value,
      user: this.editForm.get(['user'])!.value,
      lease: this.editForm.get(['lease'])!.value,
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
