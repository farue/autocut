import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ITeamMember, TeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from 'app/entities/team/team.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

type SelectableEntity = ITeam | ITenant;

@Component({
  selector: 'jhi-team-member-update',
  templateUrl: './team-member-update.component.html'
})
export class TeamMemberUpdateComponent implements OnInit {
  isSaving = false;

  teams: ITeam[] = [];

  tenants: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    role: [],
    team: [null, Validators.required],
    tenant: []
  });

  constructor(
    protected teamMemberService: TeamMemberService,
    protected teamService: TeamService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamMember }) => {
      this.updateForm(teamMember);

      this.teamService
        .query()
        .pipe(
          map((res: HttpResponse<ITeam[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: ITeam[]) => (this.teams = resBody));

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

  updateForm(teamMember: ITeamMember): void {
    this.editForm.patchValue({
      id: teamMember.id,
      role: teamMember.role,
      team: teamMember.team,
      tenant: teamMember.tenant
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
      team: this.editForm.get(['team'])!.value,
      tenant: this.editForm.get(['tenant'])!.value
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
