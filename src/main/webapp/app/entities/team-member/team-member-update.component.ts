import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { ITeamMember, TeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from 'app/entities/team/team.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

@Component({
  selector: 'jhi-team-member-update',
  templateUrl: './team-member-update.component.html'
})
export class TeamMemberUpdateComponent implements OnInit {
  isSaving: boolean;

  teams: ITeam[];

  tenants: ITenant[];

  editForm = this.fb.group({
    id: [],
    role: [],
    team: [],
    tenant: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected teamMemberService: TeamMemberService,
    protected teamService: TeamService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ teamMember }) => {
      this.updateForm(teamMember);
    });
    this.teamService
      .query()
      .subscribe((res: HttpResponse<ITeam[]>) => (this.teams = res.body), (res: HttpErrorResponse) => this.onError(res.message));
    this.tenantService
      .query()
      .subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(teamMember: ITeamMember) {
    this.editForm.patchValue({
      id: teamMember.id,
      role: teamMember.role,
      team: teamMember.team,
      tenant: teamMember.tenant
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      role: this.editForm.get(['role']).value,
      team: this.editForm.get(['team']).value,
      tenant: this.editForm.get(['tenant']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamMember>>) {
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

  trackTeamById(index: number, item: ITeam) {
    return item.id;
  }

  trackTenantById(index: number, item: ITenant) {
    return item.id;
  }
}
