import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITeamMembership } from 'app/shared/model/team-membership.model';

@Component({
  selector: 'jhi-team-membership-detail',
  templateUrl: './team-membership-detail.component.html',
})
export class TeamMembershipDetailComponent implements OnInit {
  teamMembership: ITeamMembership | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamMembership }) => (this.teamMembership = teamMembership));
  }

  previousState(): void {
    window.history.back();
  }
}
