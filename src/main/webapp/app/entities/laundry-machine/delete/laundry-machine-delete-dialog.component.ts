import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILaundryMachine } from '../laundry-machine.model';
import { LaundryMachineService } from '../service/laundry-machine.service';

@Component({
  templateUrl: './laundry-machine-delete-dialog.component.html',
})
export class LaundryMachineDeleteDialogComponent {
  laundryMachine?: ILaundryMachine;

  constructor(protected laundryMachineService: LaundryMachineService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.laundryMachineService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
