import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INetworkSwitch } from '../network-switch.model';
import { NetworkSwitchService } from '../service/network-switch.service';

@Component({
  templateUrl: './network-switch-delete-dialog.component.html',
})
export class NetworkSwitchDeleteDialogComponent {
  networkSwitch?: INetworkSwitch;

  constructor(protected networkSwitchService: NetworkSwitchService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.networkSwitchService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
