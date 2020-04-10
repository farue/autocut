import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';

@Component({
  templateUrl: './internet-access-delete-dialog.component.html'
})
export class InternetAccessDeleteDialogComponent {
  internetAccess?: IInternetAccess;

  constructor(
    protected internetAccessService: InternetAccessService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.internetAccessService.delete(id).subscribe(() => {
      this.eventManager.broadcast('internetAccessListModification');
      this.activeModal.close();
    });
  }
}
