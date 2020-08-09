import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';
import { TenantCommunicationService } from './tenant-communication.service';
import { TenantCommunicationDeleteDialogComponent } from './tenant-communication-delete-dialog.component';

@Component({
  selector: 'jhi-tenant-communication',
  templateUrl: './tenant-communication.component.html',
})
export class TenantCommunicationComponent implements OnInit, OnDestroy {
  tenantCommunications?: ITenantCommunication[];
  eventSubscriber?: Subscription;

  constructor(
    protected tenantCommunicationService: TenantCommunicationService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.tenantCommunicationService
      .query()
      .subscribe((res: HttpResponse<ITenantCommunication[]>) => (this.tenantCommunications = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTenantCommunications();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITenantCommunication): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType = '', base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
  }

  registerChangeInTenantCommunications(): void {
    this.eventSubscriber = this.eventManager.subscribe('tenantCommunicationListModification', () => this.loadAll());
  }

  delete(tenantCommunication: ITenantCommunication): void {
    const modalRef = this.modalService.open(TenantCommunicationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tenantCommunication = tenantCommunication;
  }
}
