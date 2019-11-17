import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { ISecurityPolicy, SecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';
import { ITeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from 'app/entities/team-member/team-member.service';

@Component({
  selector: 'jhi-security-policy-update',
  templateUrl: './security-policy-update.component.html'
})
export class SecurityPolicyUpdateComponent implements OnInit {
  isSaving: boolean;

  teammembers: ITeamMember[];

  editForm = this.fb.group({
    id: [],
    protectionUnit: [null, [Validators.required]],
    access: [null, [Validators.required]],
    teamMember: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected securityPolicyService: SecurityPolicyService,
    protected teamMemberService: TeamMemberService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ securityPolicy }) => {
      this.updateForm(securityPolicy);
    });
    this.teamMemberService
      .query()
      .subscribe(
        (res: HttpResponse<ITeamMember[]>) => (this.teammembers = res.body),
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  updateForm(securityPolicy: ISecurityPolicy) {
    this.editForm.patchValue({
      id: securityPolicy.id,
      protectionUnit: securityPolicy.protectionUnit,
      access: securityPolicy.access,
      teamMember: securityPolicy.teamMember
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      protectionUnit: this.editForm.get(['protectionUnit']).value,
      access: this.editForm.get(['access']).value,
      teamMember: this.editForm.get(['teamMember']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecurityPolicy>>) {
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

  trackTeamMemberById(index: number, item: ITeamMember) {
    return item.id;
  }
}
