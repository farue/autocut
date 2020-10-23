import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ITeamMembership, TeamMembership } from 'app/shared/model/team-membership.model';
import { TeamMembershipService } from './team-membership.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from 'app/entities/team/team.service';

type SelectableEntity = ITenant | ITeam;

@Component({
  selector: 'jhi-team-membership-update',
  templateUrl: './team-membership-update.component.html',
})
export class TeamMembershipUpdateComponent implements OnInit {
  isSaving = false;
  tenants: ITenant[] = [];
  teams: ITeam[] = [];
  startDp: any;
  endDp: any;

  editForm = this.fb.group({
    id: [],
    role: [],
    start: [],
    end: [],
    tenant: [],
    team: [null, Validators.required],
  });

  constructor(
    protected teamMembershipService: TeamMembershipService,
    protected tenantService: TenantService,
    protected teamService: TeamService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamMembership }) => {
      this.updateForm(teamMembership);

      this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));

      this.teamService.query().subscribe((res: HttpResponse<ITeam[]>) => (this.teams = res.body || []));
    });
  }

  updateForm(teamMembership: ITeamMembership): void {
    this.editForm.patchValue({
      id: teamMembership.id,
      role: teamMembership.role,
      start: teamMembership.start,
      end: teamMembership.end,
      tenant: teamMembership.tenant,
      team: teamMembership.team,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamMembership = this.createFromForm();
    if (teamMembership.id !== undefined) {
      this.subscribeToSaveResponse(this.teamMembershipService.update(teamMembership));
    } else {
      this.subscribeToSaveResponse(this.teamMembershipService.create(teamMembership));
    }
  }

  private createFromForm(): ITeamMembership {
    return {
      ...new TeamMembership(),
      id: this.editForm.get(['id'])!.value,
      role: this.editForm.get(['role'])!.value,
      start: this.editForm.get(['start'])!.value,
      end: this.editForm.get(['end'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
      team: this.editForm.get(['team'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamMembership>>): void {
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
