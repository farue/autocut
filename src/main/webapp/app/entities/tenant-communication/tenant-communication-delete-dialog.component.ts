import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';

@Component({
  selector: 'jhi-tenant-communication-delete-dialog',
  templateUrl: './tenant-communication-delete-dialog.component.html'
})
export class TenantCommunicationDeleteDialogComponent {
  tenantCommunication: ITenantCommunication;

  constructor(
    protected tenantCommunicationService: TenantCommunicationService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.tenantCommunicationService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'tenantCommunicationListModification',
        content: 'Deleted an tenantCommunication'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-tenant-communication-delete-popup',
  template: ''
})
export class TenantCommunicationDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ tenantCommunication }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(TenantCommunicationDeleteDialogComponent as Component, {
          size: 'lg',
          backdrop: 'static'
        });
        this.ngbModalRef.componentInstance.tenantCommunication = tenantCommunication;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/tenant-communication', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/tenant-communication', { outlets: { popup: null } }]);
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
