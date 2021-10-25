import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IInternetAccess } from '../internet-access.model';
import { InternetAccessService } from '../service/internet-access.service';

@Component({
  templateUrl: './internet-access-delete-dialog.component.html',
})
export class InternetAccessDeleteDialogComponent {
  internetAccess?: IInternetAccess;

  constructor(protected internetAccessService: InternetAccessService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.internetAccessService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
