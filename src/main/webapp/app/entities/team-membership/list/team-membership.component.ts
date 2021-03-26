import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeamMembership } from '../team-membership.model';
import { TeamMembershipService } from '../service/team-membership.service';
import { TeamMembershipDeleteDialogComponent } from '../delete/team-membership-delete-dialog.component';

@Component({
  selector: 'jhi-team-membership',
  templateUrl: './team-membership.component.html',
})
export class TeamMembershipComponent implements OnInit {
  teamMemberships?: ITeamMembership[];
  isLoading = false;

  constructor(protected teamMembershipService: TeamMembershipService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.teamMembershipService.query().subscribe(
      (res: HttpResponse<ITeamMembership[]>) => {
        this.isLoading = false;
        this.teamMemberships = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITeamMembership): number {
    return item.id!;
  }

  delete(teamMembership: ITeamMembership): void {
    const modalRef = this.modalService.open(TeamMembershipDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.teamMembership = teamMembership;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
