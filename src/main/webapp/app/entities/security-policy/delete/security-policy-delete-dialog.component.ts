import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISecurityPolicy } from '../security-policy.model';
import { SecurityPolicyService } from '../service/security-policy.service';

@Component({
  templateUrl: './security-policy-delete-dialog.component.html',
})
export class SecurityPolicyDeleteDialogComponent {
  securityPolicy?: ISecurityPolicy;

  constructor(protected securityPolicyService: SecurityPolicyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.securityPolicyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
