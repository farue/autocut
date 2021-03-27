import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISecurityPolicy } from '../security-policy.model';
import { SecurityPolicyService } from '../service/security-policy.service';
import { SecurityPolicyDeleteDialogComponent } from '../delete/security-policy-delete-dialog.component';

@Component({
  selector: 'jhi-security-policy',
  templateUrl: './security-policy.component.html',
})
export class SecurityPolicyComponent implements OnInit {
  securityPolicies?: ISecurityPolicy[];
  isLoading = false;

  constructor(protected securityPolicyService: SecurityPolicyService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.securityPolicyService.query().subscribe(
      (res: HttpResponse<ISecurityPolicy[]>) => {
        this.isLoading = false;
        this.securityPolicies = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ISecurityPolicy): number {
    return item.id!;
  }

  delete(securityPolicy: ISecurityPolicy): void {
    const modalRef = this.modalService.open(SecurityPolicyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.securityPolicy = securityPolicy;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
