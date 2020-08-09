import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { LaundryMachineProgramService } from './laundry-machine-program.service';

@Component({
  templateUrl: './laundry-machine-program-delete-dialog.component.html',
})
export class LaundryMachineProgramDeleteDialogComponent {
  laundryMachineProgram?: ILaundryMachineProgram;

  constructor(
    protected laundryMachineProgramService: LaundryMachineProgramService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.laundryMachineProgramService.delete(id).subscribe(() => {
      this.eventManager.broadcast('laundryMachineProgramListModification');
      this.activeModal.close();
    });
  }
}
