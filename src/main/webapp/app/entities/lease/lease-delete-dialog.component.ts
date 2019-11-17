import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';

@Component({
  selector: 'jhi-lease-delete-dialog',
  templateUrl: './lease-delete-dialog.component.html'
})
export class LeaseDeleteDialogComponent {
  lease: ILease;

  constructor(protected leaseService: LeaseService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.leaseService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'leaseListModification',
        content: 'Deleted an lease'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-lease-delete-popup',
  template: ''
})
export class LeaseDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ lease }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(LeaseDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.lease = lease;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/lease', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/lease', { outlets: { popup: null } }]);
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
