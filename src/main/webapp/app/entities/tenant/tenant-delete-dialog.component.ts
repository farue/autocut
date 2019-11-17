import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';

@Component({
  selector: 'jhi-tenant-delete-dialog',
  templateUrl: './tenant-delete-dialog.component.html'
})
export class TenantDeleteDialogComponent {
  tenant: ITenant;

  constructor(protected tenantService: TenantService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.tenantService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'tenantListModification',
        content: 'Deleted an tenant'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-tenant-delete-popup',
  template: ''
})
export class TenantDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ tenant }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(TenantDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.tenant = tenant;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/tenant', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/tenant', { outlets: { popup: null } }]);
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
