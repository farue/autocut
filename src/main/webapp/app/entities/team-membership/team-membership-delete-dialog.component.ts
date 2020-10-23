import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITeamMembership } from 'app/shared/model/team-membership.model';
import { TeamMembershipService } from './team-membership.service';

@Component({
  templateUrl: './team-membership-delete-dialog.component.html',
})
export class TeamMembershipDeleteDialogComponent {
  teamMembership?: ITeamMembership;

  constructor(
    protected teamMembershipService: TeamMembershipService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamMembershipService.delete(id).subscribe(() => {
      this.eventManager.broadcast('teamMembershipListModification');
      this.activeModal.close();
    });
  }
}
