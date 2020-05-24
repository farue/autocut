import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';

@Component({
  templateUrl: './tenant-communication-delete-dialog.component.html',
})
export class TenantCommunicationDeleteDialogComponent {
  tenantCommunication?: ITenantCommunication;

  constructor(
    protected tenantCommunicationService: TenantCommunicationService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantCommunicationService.delete(id).subscribe(() => {
      this.eventManager.broadcast('tenantCommunicationListModification');
      this.activeModal.close();
    });
  }
}
