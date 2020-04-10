import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineService } from './laundry-machine.service';

@Component({
  templateUrl: './laundry-machine-delete-dialog.component.html'
})
export class LaundryMachineDeleteDialogComponent {
  laundryMachine?: ILaundryMachine;

  constructor(
    protected laundryMachineService: LaundryMachineService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.laundryMachineService.delete(id).subscribe(() => {
      this.eventManager.broadcast('laundryMachineListModification');
      this.activeModal.close();
    });
  }
}
