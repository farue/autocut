import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITeamMember } from 'app/shared/model/team-member.model';

@Component({
  selector: 'jhi-team-member-detail',
  templateUrl: './team-member-detail.component.html'
})
export class TeamMemberDetailComponent implements OnInit {
  teamMember: ITeamMember;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ teamMember }) => {
      this.teamMember = teamMember;
    });
  }

  previousState() {
    window.history.back();
  }
}
