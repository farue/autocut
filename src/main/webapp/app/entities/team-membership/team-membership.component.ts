import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeamMembership } from 'app/shared/model/team-membership.model';
import { TeamMembershipService } from './team-membership.service';
import { TeamMembershipDeleteDialogComponent } from './team-membership-delete-dialog.component';

@Component({
  selector: 'jhi-team-membership',
  templateUrl: './team-membership.component.html',
})
export class TeamMembershipComponent implements OnInit, OnDestroy {
  teamMemberships?: ITeamMembership[];
  eventSubscriber?: Subscription;

  constructor(
    protected teamMembershipService: TeamMembershipService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.teamMembershipService.query().subscribe((res: HttpResponse<ITeamMembership[]>) => (this.teamMemberships = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTeamMemberships();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITeamMembership): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTeamMemberships(): void {
    this.eventSubscriber = this.eventManager.subscribe('teamMembershipListModification', () => this.loadAll());
  }

  delete(teamMembership: ITeamMembership): void {
    const modalRef = this.modalService.open(TeamMembershipDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.teamMembership = teamMembership;
  }
}
