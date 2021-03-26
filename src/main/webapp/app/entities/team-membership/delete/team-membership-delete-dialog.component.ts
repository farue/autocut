import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeamMembership } from '../team-membership.model';
import { TeamMembershipService } from '../service/team-membership.service';

@Component({
  templateUrl: './team-membership-delete-dialog.component.html',
})
export class TeamMembershipDeleteDialogComponent {
  teamMembership?: ITeamMembership;

  constructor(protected teamMembershipService: TeamMembershipService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamMembershipService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
