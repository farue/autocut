import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITeamMembership, TeamMembership } from '../team-membership.model';
import { TeamMembershipService } from '../service/team-membership.service';
import { ITenant } from 'app/entities/tenant/tenant.model';
import { TenantService } from 'app/entities/tenant/service/tenant.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { TeamRole } from 'app/entities/enumerations/team-role.model';

@Component({
  selector: 'jhi-team-membership-update',
  templateUrl: './team-membership-update.component.html',
})
export class TeamMembershipUpdateComponent implements OnInit {
  isSaving = false;
  teamRoleValues = Object.keys(TeamRole);

  tenantsSharedCollection: ITenant[] = [];
  teamsSharedCollection: ITeam[] = [];

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
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamMembership }) => {
      this.updateForm(teamMembership);

      this.loadRelationshipsOptions();
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

  trackTenantById(index: number, item: ITenant): number {
    return item.id!;
  }

  trackTeamById(index: number, item: ITeam): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamMembership>>): void {
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

  protected updateForm(teamMembership: ITeamMembership): void {
    this.editForm.patchValue({
      id: teamMembership.id,
      role: teamMembership.role,
      start: teamMembership.start,
      end: teamMembership.end,
      tenant: teamMembership.tenant,
      team: teamMembership.team,
    });

    this.tenantsSharedCollection = this.tenantService.addTenantToCollectionIfMissing(this.tenantsSharedCollection, teamMembership.tenant);
    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing(this.teamsSharedCollection, teamMembership.team);
  }

  protected loadRelationshipsOptions(): void {
    this.tenantService
      .query()
      .pipe(map((res: HttpResponse<ITenant[]>) => res.body ?? []))
      .pipe(map((tenants: ITenant[]) => this.tenantService.addTenantToCollectionIfMissing(tenants, this.editForm.get('tenant')!.value)))
      .subscribe((tenants: ITenant[]) => (this.tenantsSharedCollection = tenants));

    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing(teams, this.editForm.get('team')!.value)))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));
  }

  protected createFromForm(): ITeamMembership {
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
}
