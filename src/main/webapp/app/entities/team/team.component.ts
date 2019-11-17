import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from './team.service';

@Component({
  selector: 'jhi-team',
  templateUrl: './team.component.html'
})
export class TeamComponent implements OnInit, OnDestroy {
  teams: ITeam[];
  eventSubscriber: Subscription;

  constructor(protected teamService: TeamService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.teamService.query().subscribe((res: HttpResponse<ITeam[]>) => {
      this.teams = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInTeams();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ITeam) {
    return item.id;
  }

  registerChangeInTeams() {
    this.eventSubscriber = this.eventManager.subscribe('teamListModification', () => this.loadAll());
  }
}
