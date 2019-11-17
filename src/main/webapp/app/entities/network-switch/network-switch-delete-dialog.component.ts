import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { INetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';

@Component({
  selector: 'jhi-network-switch-delete-dialog',
  templateUrl: './network-switch-delete-dialog.component.html'
})
export class NetworkSwitchDeleteDialogComponent {
  networkSwitch: INetworkSwitch;

  constructor(
    protected networkSwitchService: NetworkSwitchService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.networkSwitchService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'networkSwitchListModification',
        content: 'Deleted an networkSwitch'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-network-switch-delete-popup',
  template: ''
})
export class NetworkSwitchDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(NetworkSwitchDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.networkSwitch = networkSwitch;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/network-switch', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/network-switch', { outlets: { popup: null } }]);
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
