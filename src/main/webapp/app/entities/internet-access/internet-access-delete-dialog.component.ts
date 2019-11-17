import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';

@Component({
  selector: 'jhi-internet-access-delete-dialog',
  templateUrl: './internet-access-delete-dialog.component.html'
})
export class InternetAccessDeleteDialogComponent {
  internetAccess: IInternetAccess;

  constructor(
    protected internetAccessService: InternetAccessService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.internetAccessService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'internetAccessListModification',
        content: 'Deleted an internetAccess'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-internet-access-delete-popup',
  template: ''
})
export class InternetAccessDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ internetAccess }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(InternetAccessDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.internetAccess = internetAccess;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/internet-access', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/internet-access', { outlets: { popup: null } }]);
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
