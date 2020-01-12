import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ISecurityPolicy, SecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';
import { ITeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from 'app/entities/team-member/team-member.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

type SelectableEntity = ITeamMember | ITenant;

@Component({
  selector: 'jhi-security-policy-update',
  templateUrl: './security-policy-update.component.html'
})
export class SecurityPolicyUpdateComponent implements OnInit {
  isSaving = false;

  teammembers: ITeamMember[] = [];

  tenants: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    protectionUnit: [null, [Validators.required]],
    access: [null, [Validators.required]],
    teamMember: [],
    tenant: []
  });

  constructor(
    protected securityPolicyService: SecurityPolicyService,
    protected teamMemberService: TeamMemberService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ securityPolicy }) => {
      this.updateForm(securityPolicy);

      this.teamMemberService
        .query()
        .pipe(
          map((res: HttpResponse<ITeamMember[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ITeamMember[]) => (this.teammembers = resBody));

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

  updateForm(securityPolicy: ISecurityPolicy): void {
    this.editForm.patchValue({
      id: securityPolicy.id,
      protectionUnit: securityPolicy.protectionUnit,
      access: securityPolicy.access,
      teamMember: securityPolicy.teamMember,
      tenant: securityPolicy.tenant
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const securityPolicy = this.createFromForm();
    if (securityPolicy.id !== undefined) {
      this.subscribeToSaveResponse(this.securityPolicyService.update(securityPolicy));
    } else {
      this.subscribeToSaveResponse(this.securityPolicyService.create(securityPolicy));
    }
  }

  private createFromForm(): ISecurityPolicy {
    return {
      ...new SecurityPolicy(),
      id: this.editForm.get(['id'])!.value,
      protectionUnit: this.editForm.get(['protectionUnit'])!.value,
      access: this.editForm.get(['access'])!.value,
      teamMember: this.editForm.get(['teamMember'])!.value,
      tenant: this.editForm.get(['tenant'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecurityPolicy>>): void {
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
