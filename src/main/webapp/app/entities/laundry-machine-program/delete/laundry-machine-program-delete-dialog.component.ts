import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {ILaundryMachineProgram} from '../laundry-machine-program.model';
import {LaundryMachineProgramService} from '../service/laundry-machine-program.service';

@Component({
  templateUrl: './laundry-machine-program-delete-dialog.component.html',
})
export class LaundryMachineProgramDeleteDialogComponent {
  laundryMachineProgram?: ILaundryMachineProgram;

  constructor(protected laundryMachineProgramService: LaundryMachineProgramService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.laundryMachineProgramService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
