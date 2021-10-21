import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {ILaundryProgram} from '../laundry-program.model';
import {LaundryProgramService} from '../service/laundry-program.service';

@Component({
  templateUrl: './laundry-program-delete-dialog.component.html',
})
export class LaundryProgramDeleteDialogComponent {
  laundryProgram?: ILaundryProgram;

  constructor(protected laundryProgramService: LaundryProgramService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.laundryProgramService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
