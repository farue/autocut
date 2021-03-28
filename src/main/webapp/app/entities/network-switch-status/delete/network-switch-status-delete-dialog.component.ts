import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INetworkSwitchStatus } from '../network-switch-status.model';
import { NetworkSwitchStatusService } from '../service/network-switch-status.service';

@Component({
  templateUrl: './network-switch-status-delete-dialog.component.html',
})
export class NetworkSwitchStatusDeleteDialogComponent {
  networkSwitchStatus?: INetworkSwitchStatus;

  constructor(protected networkSwitchStatusService: NetworkSwitchStatusService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.networkSwitchStatusService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
