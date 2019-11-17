import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPort } from 'app/shared/model/port.model';
import { PortService } from './port.service';

@Component({
  selector: 'jhi-port-delete-dialog',
  templateUrl: './port-delete-dialog.component.html'
})
export class PortDeleteDialogComponent {
  port: IPort;

  constructor(protected portService: PortService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.portService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'portListModification',
        content: 'Deleted an port'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-port-delete-popup',
  template: ''
})
export class PortDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ port }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(PortDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.port = port;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/port', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/port', { outlets: { popup: null } }]);
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
