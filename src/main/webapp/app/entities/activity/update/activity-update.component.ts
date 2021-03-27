import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IActivity, Activity } from '../activity.model';
import { ActivityService } from '../service/activity.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { ITeamMembership } from 'app/entities/team-membership/team-membership.model';
import { TeamMembershipService } from 'app/entities/team-membership/service/team-membership.service';

@Component({
  selector: 'jhi-activity-update',
  templateUrl: './activity-update.component.html',
})
export class ActivityUpdateComponent implements OnInit {
  isSaving = false;

  tenantsSharedCollection: ITenant[] = [];
  teamMembershipsSharedCollection: ITeamMembership[] = [];

  editForm = this.fb.group({
    id: [],
    year: [null, [Validators.required]],
    term: [null, [Validators.required]],
    start: [],
    end: [],
    description: [],
    discount: [],
    stwActivity: [],
    tenant: [],
    teamMembership: [],
  });

  constructor(
    protected activityService: ActivityService,
    protected tenantService: TenantService,
    protected teamMembershipService: TeamMembershipService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activity }) => {
      this.updateForm(activity);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const activity = this.createFromForm();
    if (activity.id !== undefined) {
      this.subscribeToSaveResponse(this.activityService.update(activity));
    } else {
      this.subscribeToSaveResponse(this.activityService.create(activity));
    }
  }

  trackTenantById(index: number, item: ITenant): number {
    return item.id!;
  }

  trackTeamMembershipById(index: number, item: ITeamMembership): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivity>>): void {
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

  protected updateForm(activity: IActivity): void {
    this.editForm.patchValue({
      id: activity.id,
      year: activity.year,
      term: activity.term,
      start: activity.start,
      end: activity.end,
      description: activity.description,
      discount: activity.discount,
      stwActivity: activity.stwActivity,
      tenant: activity.tenant,
      teamMembership: activity.teamMembership,
    });

    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing(this.tenantsSharedCollection, activity.tenant);
    this.teamMembershipsSharedCollection = this.teamMembershipService.addTeamMembershipToCollectionIfMissing(
      this.teamMembershipsSharedCollection,
      activity.teamMembership
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing(tenants, this.editForm.get('tenant')!.value)))
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));

    this.teamMembershipService
      .query()
      .pipe(map((res: HttpResponse<ITeamMembership[]>) => res.body ?? []))
      .pipe(
        map((teamMemberships: ITeamMembership[]) =>
          this.teamMembershipService.addTeamMembershipToCollectionIfMissing(teamMemberships, this.editForm.get('teamMembership')!.value)
        )
      )
      .subscribe((teamMemberships: ITeamMembership[]) => (this.teamMembershipsSharedCollection = teamMemberships));
  }

  protected createFromForm(): IActivity {
    return {
      ...new Activity(),
      id: this.editForm.get(['id'])!.value,
      year: this.editForm.get(['year'])!.value,
      term: this.editForm.get(['term'])!.value,
      start: this.editForm.get(['start'])!.value,
      end: this.editForm.get(['end'])!.value,
      description: this.editForm.get(['description'])!.value,
      discount: this.editForm.get(['discount'])!.value,
      stwActivity: this.editForm.get(['stwActivity'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
      teamMembership: this.editForm.get(['teamMembership'])!.value,
    };
  }
}
