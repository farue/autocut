import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IRegistrationItem } from 'app/shared/model/registration-item.model';
import { RegistrationItemService } from './registration-item.service';

@Component({
  templateUrl: './registration-item-delete-dialog.component.html',
})
export class RegistrationItemDeleteDialogComponent {
  registrationItem?: IRegistrationItem;

  constructor(
    protected registrationItemService: RegistrationItemService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.registrationItemService.delete(id).subscribe(() => {
      this.eventManager.broadcast('registrationItemListModification');
      this.activeModal.close();
    });
  }
}
