import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';

@Component({
  templateUrl: './tenant-delete-dialog.component.html'
})
export class TenantDeleteDialogComponent {
  tenant?: ITenant;

  constructor(protected tenantService: TenantService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantService.delete(id).subscribe(() => {
      this.eventManager.broadcast('tenantListModification');
      this.activeModal.close();
    });
  }
}
