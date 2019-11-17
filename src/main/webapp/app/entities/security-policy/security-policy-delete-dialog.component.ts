import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';

@Component({
  selector: 'jhi-security-policy-delete-dialog',
  templateUrl: './security-policy-delete-dialog.component.html'
})
export class SecurityPolicyDeleteDialogComponent {
  securityPolicy: ISecurityPolicy;

  constructor(
    protected securityPolicyService: SecurityPolicyService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.securityPolicyService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'securityPolicyListModification',
        content: 'Deleted an securityPolicy'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-security-policy-delete-popup',
  template: ''
})
export class SecurityPolicyDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ securityPolicy }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(SecurityPolicyDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.securityPolicy = securityPolicy;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/security-policy', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/security-policy', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
