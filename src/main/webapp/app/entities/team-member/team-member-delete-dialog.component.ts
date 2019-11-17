import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITeamMember } from 'app/shared/model/team-member.model';
import { TeamMemberService } from './team-member.service';

@Component({
  selector: 'jhi-team-member-delete-dialog',
  templateUrl: './team-member-delete-dialog.component.html'
})
export class TeamMemberDeleteDialogComponent {
  teamMember: ITeamMember;

  constructor(
    protected teamMemberService: TeamMemberService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.teamMemberService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'teamMemberListModification',
        content: 'Deleted an teamMember'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-team-member-delete-popup',
  template: ''
})
export class TeamMemberDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ teamMember }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(TeamMemberDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.teamMember = teamMember;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/team-member', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/team-member', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
