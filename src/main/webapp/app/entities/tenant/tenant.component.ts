import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from './tenant.service';
import { TenantDeleteDialogComponent } from './tenant-delete-dialog.component';

@Component({
  selector: 'jhi-tenant',
  templateUrl: './tenant.component.html'
})
export class TenantComponent implements OnInit, OnDestroy {
  tenants?: ITenant[];
  eventSubscriber?: Subscription;

  constructor(protected tenantService: TenantService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTenants();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITenant): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTenants(): void {
    this.eventSubscriber = this.eventManager.subscribe('tenantListModification', () => this.loadAll());
  }

  delete(tenant: ITenant): void {
    const modalRef = this.modalService.open(TenantDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tenant = tenant;
  }
}
