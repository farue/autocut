import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILease } from '../lease.model';
import { LeaseService } from '../service/lease.service';

@Component({
  templateUrl: './lease-delete-dialog.component.html',
})
export class LeaseDeleteDialogComponent {
  lease?: ILease;

  constructor(protected leaseService: LeaseService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.leaseService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
