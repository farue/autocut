import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ITeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';

@Component({
  selector: 'jhi-team-member',
  templateUrl: './team-member.component.html'
})
export class TeamMemberComponent implements OnInit, OnDestroy {
  teamMembers: ITeamMember[];
  eventSubscriber: Subscription;

  constructor(protected teamMemberService: TeamMemberService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.teamMemberService.query().subscribe((res: HttpResponse<ITeamMember[]>) => {
      this.teamMembers = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInTeamMembers();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ITeamMember) {
    return item.id;
  }

  registerChangeInTeamMembers() {
    this.eventSubscriber = this.eventManager.subscribe('teamMemberListModification', () => this.loadAll());
  }
}
