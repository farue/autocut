import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';

@Component({
  templateUrl: './team-member-delete-dialog.component.html'
})
export class TeamMemberDeleteDialogComponent {
  teamMember?: ITeamMember;

  constructor(
    protected teamMemberService: TeamMemberService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamMemberService.delete(id).subscribe(() => {
      this.eventManager.broadcast('teamMemberListModification');
      this.activeModal.close();
    });
  }
}
