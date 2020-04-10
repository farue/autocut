import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';

@Component({
  templateUrl: './security-policy-delete-dialog.component.html'
})
export class SecurityPolicyDeleteDialogComponent {
  securityPolicy?: ISecurityPolicy;

  constructor(
    protected securityPolicyService: SecurityPolicyService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.securityPolicyService.delete(id).subscribe(() => {
      this.eventManager.broadcast('securityPolicyListModification');
      this.activeModal.close();
    });
  }
}
