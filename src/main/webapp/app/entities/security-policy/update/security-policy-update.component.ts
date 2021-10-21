import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize, map} from 'rxjs/operators';

import {ISecurityPolicy, SecurityPolicy} from '../security-policy.model';
import {SecurityPolicyService} from '../service/security-policy.service';
import {ITeamMembership} from 'app/entities/team-membership/team-membership.model';
import {TeamMembershipService} from 'app/entities/team-membership/service/team-membership.service';
import {ITenant} from 'app/entities/tenant/tenant.model';
import {TenantService} from 'app/entities/tenant/service/tenant.service';
import {ProtectionUnits} from 'app/entities/enumerations/protection-units.model';
import {Access} from 'app/entities/enumerations/access.model';

@Component({
  selector: 'jhi-security-policy-update',
  templateUrl: './security-policy-update.component.html',
})
export class SecurityPolicyUpdateComponent implements OnInit {
  isSaving = false;
  protectionUnitsValues = Object.keys(ProtectionUnits);
  accessValues = Object.keys(Access);

  teamMembershipsSharedCollection: ITeamMembership[] = [];
  tenantsSharedCollection: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    protectionUnit: [null, [Validators.required]],
    access: [null, [Validators.required]],
    teamMember: [],
    tenant: [],
  });

  constructor(
    protected securityPolicyService: SecurityPolicyService,
    protected teamMembershipService: TeamMembershipService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ securityPolicy }) => {
      this.updateForm(securityPolicy);

      this.loadRelationshipsOptions();
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

  trackTeamMembershipById(index: number, item: ITeamMembership): number {
    return item.id!;
  }

  trackTenantById(index: number, item: ITenant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecurityPolicy>>): void {
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

  protected updateForm(securityPolicy: ISecurityPolicy): void {
    this.editForm.patchValue({
      id: securityPolicy.id,
      protectionUnit: securityPolicy.protectionUnit,
      access: securityPolicy.access,
      teamMember: securityPolicy.teamMember,
      tenant: securityPolicy.tenant,
    });

    this.teamMembershipsSharedCollection = this.teamMembershipService.addTeamMembershipToCollectionIfMissing(
      this.teamMembershipsSharedCollection,
      securityPolicy.teamMember
    );
    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing(this.tenantsSharedCollection, securityPolicy.tenant);
  }

  protected loadRelationshipsOptions(): void {
    this.teamMembershipService
      .query()
      .pipe(map((res: HttpResponse<ITeamMembership[]>) => res.body ?? []))
      .pipe(
        map((teamMemberships: ITeamMembership[]) =>
          this.teamMembershipService.addTeamMembershipToCollectionIfMissing(teamMemberships, this.editForm.get('teamMember')!.value)
        )
      )
      .subscribe((teamMemberships: ITeamMembership[]) => (this.teamMembershipsSharedCollection = teamMemberships));

    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing(tenants, this.editForm.get('tenant')!.value)))
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));
  }

  protected createFromForm(): ISecurityPolicy {
    return {
      ...new SecurityPolicy(),
      id: this.editForm.get(['id'])!.value,
      protectionUnit: this.editForm.get(['protectionUnit'])!.value,
      access: this.editForm.get(['access'])!.value,
      teamMember: this.editForm.get(['teamMember'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
    };
  }
}
