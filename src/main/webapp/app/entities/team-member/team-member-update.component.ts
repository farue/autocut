import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ITeamMember, TeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from 'app/entities/team/team.service';
import { IActivity } from 'app/shared/model/activity.model';
import { ActivityService } from 'app/entities/activity/activity.service';

type SelectableEntity = ITenant | ITeam | IActivity;

@Component({
  selector: 'jhi-team-member-update',
  templateUrl: './team-member-update.component.html'
})
export class TeamMemberUpdateComponent implements OnInit {
  isSaving = false;
  tenants: ITenant[] = [];
  teams: ITeam[] = [];
  activities: IActivity[] = [];

  editForm = this.fb.group({
    id: [],
    role: [],
    tenant: [],
    team: [null, Validators.required],
    activity: []
  });

  constructor(
    protected teamMemberService: TeamMemberService,
    protected tenantService: TenantService,
    protected teamService: TeamService,
    protected activityService: ActivityService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamMember }) => {
      this.updateForm(teamMember);

      this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));

      this.teamService.query().subscribe((res: HttpResponse<ITeam[]>) => (this.teams = res.body || []));

      this.activityService.query().subscribe((res: HttpResponse<IActivity[]>) => (this.activities = res.body || []));
    });
  }

  updateForm(teamMember: ITeamMember): void {
    this.editForm.patchValue({
      id: teamMember.id,
      role: teamMember.role,
      tenant: teamMember.tenant,
      team: teamMember.team,
      activity: teamMember.activity
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamMember = this.createFromForm();
    if (teamMember.id !== undefined) {
      this.subscribeToSaveResponse(this.teamMemberService.update(teamMember));
    } else {
      this.subscribeToSaveResponse(this.teamMemberService.create(teamMember));
    }
  }

  private createFromForm(): ITeamMember {
    return {
      ...new TeamMember(),
      id: this.editForm.get(['id'])!.value,
      role: this.editForm.get(['role'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
      team: this.editForm.get(['team'])!.value,
      activity: this.editForm.get(['activity'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamMember>>): void {
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
