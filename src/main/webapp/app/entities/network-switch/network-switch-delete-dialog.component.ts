import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { INetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';

@Component({
  templateUrl: './network-switch-delete-dialog.component.html'
})
export class NetworkSwitchDeleteDialogComponent {
  networkSwitch?: INetworkSwitch;

  constructor(
    protected networkSwitchService: NetworkSwitchService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.networkSwitchService.delete(id).subscribe(() => {
      this.eventManager.broadcast('networkSwitchListModification');
      this.activeModal.close();
    });
  }
}
