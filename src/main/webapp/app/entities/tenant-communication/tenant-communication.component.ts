import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiDataUtils, JhiEventManager } from 'ng-jhipster';

import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';

@Component({
  selector: 'jhi-tenant-communication',
  templateUrl: './tenant-communication.component.html'
})
export class TenantCommunicationComponent implements OnInit, OnDestroy {
  tenantCommunications: ITenantCommunication[];
  eventSubscriber: Subscription;

  constructor(
    protected tenantCommunicationService: TenantCommunicationService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager
  ) {}

  loadAll() {
    this.tenantCommunicationService.query().subscribe((res: HttpResponse<ITenantCommunication[]>) => {
      this.tenantCommunications = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInTenantCommunications();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ITenantCommunication) {
    return item.id;
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  registerChangeInTenantCommunications() {
    this.eventSubscriber = this.eventManager.subscribe('tenantCommunicationListModification', () => this.loadAll());
  }
}
