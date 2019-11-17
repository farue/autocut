import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { ITenant, Tenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from 'app/entities/lease/lease.service';

@Component({
  selector: 'jhi-tenant-update',
  templateUrl: './tenant-update.component.html'
})
export class TenantUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  leases: ILease[];

  editForm = this.fb.group({
    id: [],
    firstName: [null, [Validators.required]],
    lastName: [null, [Validators.required]],
    email: [null, [Validators.required]],
    user: [],
    lease: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected tenantService: TenantService,
    protected userService: UserService,
    protected leaseService: LeaseService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ tenant }) => {
      this.updateForm(tenant);
    });
    this.userService
      .query()
      .subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body), (res: HttpErrorResponse) => this.onError(res.message));
    this.leaseService
      .query()
      .subscribe((res: HttpResponse<ILease[]>) => (this.leases = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(tenant: ITenant) {
    this.editForm.patchValue({
      id: tenant.id,
      firstName: tenant.firstName,
      lastName: tenant.lastName,
      email: tenant.email,
      user: tenant.user,
      lease: tenant.lease
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      firstName: this.editForm.get(['firstName']).value,
      lastName: this.editForm.get(['lastName']).value,
      email: this.editForm.get(['email']).value,
      user: this.editForm.get(['user']).value,
      lease: this.editForm.get(['lease']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITenant>>) {
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

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  trackLeaseById(index: number, item: ILease) {
    return item.id;
  }
}
