import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';

@Component({
  selector: 'jhi-tenant',
  templateUrl: './tenant.component.html'
})
export class TenantComponent implements OnInit, OnDestroy {
  tenants: ITenant[];
  eventSubscriber: Subscription;

  constructor(protected tenantService: TenantService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => {
      this.tenants = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInTenants();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ITenant) {
    return item.id;
  }

  registerChangeInTenants() {
    this.eventSubscriber = this.eventManager.subscribe('tenantListModification', () => this.loadAll());
  }
}
