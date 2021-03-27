import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IApartment } from '../apartment.model';
import { ApartmentService } from '../service/apartment.service';

@Component({
  templateUrl: './apartment-delete-dialog.component.html',
})
export class ApartmentDeleteDialogComponent {
  apartment?: IApartment;

  constructor(protected apartmentService: ApartmentService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.apartmentService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
