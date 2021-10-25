import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITenantCommunication } from '../tenant-communication.model';
import { TenantCommunicationService } from '../service/tenant-communication.service';

@Component({
  templateUrl: './tenant-communication-delete-dialog.component.html',
})
export class TenantCommunicationDeleteDialogComponent {
  tenantCommunication?: ITenantCommunication;

  constructor(protected tenantCommunicationService: TenantCommunicationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantCommunicationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
