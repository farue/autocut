import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';
import { SecurityPolicyDeleteDialogComponent } from './security-policy-delete-dialog.component';

@Component({
  selector: 'jhi-security-policy',
  templateUrl: './security-policy.component.html'
})
export class SecurityPolicyComponent implements OnInit, OnDestroy {
  securityPolicies?: ISecurityPolicy[];
  eventSubscriber?: Subscription;

  constructor(
    protected securityPolicyService: SecurityPolicyService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.securityPolicyService.query().subscribe((res: HttpResponse<ISecurityPolicy[]>) => {
      this.securityPolicies = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInSecurityPolicies();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ISecurityPolicy): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInSecurityPolicies(): void {
    this.eventSubscriber = this.eventManager.subscribe('securityPolicyListModification', () => this.loadAll());
  }

  delete(securityPolicy: ISecurityPolicy): void {
    const modalRef = this.modalService.open(SecurityPolicyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.securityPolicy = securityPolicy;
  }
}
