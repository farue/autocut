import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ISecurityPolicy } from 'app/shared/model/security-policy.model';
import { SecurityPolicyService } from './security-policy.service';

@Component({
  selector: 'jhi-security-policy',
  templateUrl: './security-policy.component.html'
})
export class SecurityPolicyComponent implements OnInit, OnDestroy {
  securityPolicies: ISecurityPolicy[];
  eventSubscriber: Subscription;

  constructor(protected securityPolicyService: SecurityPolicyService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.securityPolicyService.query().subscribe((res: HttpResponse<ISecurityPolicy[]>) => {
      this.securityPolicies = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInSecurityPolicies();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ISecurityPolicy) {
    return item.id;
  }

  registerChangeInSecurityPolicies() {
    this.eventSubscriber = this.eventManager.subscribe('securityPolicyListModification', () => this.loadAll());
  }
}
