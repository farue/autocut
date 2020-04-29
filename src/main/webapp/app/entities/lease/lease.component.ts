import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';
import { LeaseDeleteDialogComponent } from './lease-delete-dialog.component';

@Component({
  selector: 'jhi-lease',
  templateUrl: './lease.component.html'
})
export class LeaseComponent implements OnInit, OnDestroy {
  leases?: ILease[];
  eventSubscriber?: Subscription;

  constructor(
    protected leaseService: LeaseService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.leaseService.query().subscribe((res: HttpResponse<ILease[]>) => (this.leases = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInLeases();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ILease): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
  }

  registerChangeInLeases(): void {
    this.eventSubscriber = this.eventManager.subscribe('leaseListModification', () => this.loadAll());
  }

  delete(lease: ILease): void {
    const modalRef = this.modalService.open(LeaseDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.lease = lease;
  }
}
