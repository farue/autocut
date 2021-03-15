import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { INetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';
import { NetworkSwitchStatusService } from './network-switch-status.service';

@Component({
  templateUrl: './network-switch-status-delete-dialog.component.html',
})
export class NetworkSwitchStatusDeleteDialogComponent {
  networkSwitchStatus?: INetworkSwitchStatus;

  constructor(
    protected networkSwitchStatusService: NetworkSwitchStatusService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.networkSwitchStatusService.delete(id).subscribe(() => {
      this.eventManager.broadcast('networkSwitchStatusListModification');
      this.activeModal.close();
    });
  }
}
