import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IActivity, Activity } from 'app/shared/model/activity.model';
import { ActivityService } from './activity.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';
import { ITeamMembership } from 'app/shared/model/team-membership.model';
import { TeamMembershipService } from 'app/entities/team-membership/team-membership.service';

type SelectableEntity = ITenant | ITeamMembership;

@Component({
  selector: 'jhi-activity-update',
  templateUrl: './activity-update.component.html',
})
export class ActivityUpdateComponent implements OnInit {
  isSaving = false;
  tenants: ITenant[] = [];
  teammemberships: ITeamMembership[] = [];
  startDp: any;
  endDp: any;

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
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activity }) => {
      this.updateForm(activity);

      this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));

      this.teamMembershipService.query().subscribe((res: HttpResponse<ITeamMembership[]>) => (this.teammemberships = res.body || []));
    });
  }

  updateForm(activity: IActivity): void {
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

  private createFromForm(): IActivity {
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivity>>): void {
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
