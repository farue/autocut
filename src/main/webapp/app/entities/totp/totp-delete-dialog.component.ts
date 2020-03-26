import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITotp } from 'app/shared/model/totp.model';
import { TotpService } from './totp.service';

@Component({
  templateUrl: './totp-delete-dialog.component.html'
})
export class TotpDeleteDialogComponent {
  totp?: ITotp;

  constructor(protected totpService: TotpService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.totpService.delete(id).subscribe(() => {
      this.eventManager.broadcast('totpListModification');
      this.activeModal.close();
    });
  }
}
