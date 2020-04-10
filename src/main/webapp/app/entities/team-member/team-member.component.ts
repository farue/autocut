import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';
import { TeamMemberDeleteDialogComponent } from './team-member-delete-dialog.component';

@Component({
  selector: 'jhi-team-member',
  templateUrl: './team-member.component.html'
})
export class TeamMemberComponent implements OnInit, OnDestroy {
  teamMembers?: ITeamMember[];
  eventSubscriber?: Subscription;

  constructor(protected teamMemberService: TeamMemberService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.teamMemberService.query().subscribe((res: HttpResponse<ITeamMember[]>) => (this.teamMembers = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTeamMembers();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITeamMember): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTeamMembers(): void {
    this.eventSubscriber = this.eventManager.subscribe('teamMemberListModification', () => this.loadAll());
  }

  delete(teamMember: ITeamMember): void {
    const modalRef = this.modalService.open(TeamMemberDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.teamMember = teamMember;
  }
}
