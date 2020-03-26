import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ITotp, Totp } from 'app/shared/model/totp.model';
import { TotpService } from './totp.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

@Component({
  selector: 'jhi-totp-update',
  templateUrl: './totp-update.component.html'
})
export class TotpUpdateComponent implements OnInit {
  isSaving = false;

  tenants: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    secret: [null, [Validators.required]],
    tenant: []
  });

  constructor(
    protected totpService: TotpService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ totp }) => {
      this.updateForm(totp);

      this.tenantService
        .query({ filter: 'totp-is-null' })
        .pipe(
          map((res: HttpResponse<ITenant[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ITenant[]) => {
          if (!totp.tenant || !totp.tenant.id) {
            this.tenants = resBody;
          } else {
            this.tenantService
              .find(totp.tenant.id)
              .pipe(
                map((subRes: HttpResponse<ITenant>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: ITenant[]) => {
                this.tenants = concatRes;
              });
          }
        });
    });
  }

  updateForm(totp: ITotp): void {
    this.editForm.patchValue({
      id: totp.id,
      secret: totp.secret,
      tenant: totp.tenant
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const totp = this.createFromForm();
    if (totp.id !== undefined) {
      this.subscribeToSaveResponse(this.totpService.update(totp));
    } else {
      this.subscribeToSaveResponse(this.totpService.create(totp));
    }
  }

  private createFromForm(): ITotp {
    return {
      ...new Totp(),
      id: this.editForm.get(['id'])!.value,
      secret: this.editForm.get(['secret'])!.value,
      tenant: this.editForm.get(['tenant'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITotp>>): void {
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
