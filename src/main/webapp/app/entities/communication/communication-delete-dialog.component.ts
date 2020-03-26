import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICommunication } from 'app/shared/model/communication.model';
import { CommunicationService } from './communication.service';

@Component({
  templateUrl: './communication-delete-dialog.component.html'
})
export class CommunicationDeleteDialogComponent {
  communication?: ICommunication;

  constructor(
    protected communicationService: CommunicationService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.communicationService.delete(id).subscribe(() => {
      this.eventManager.broadcast('communicationListModification');
      this.activeModal.close();
    });
  }
}
