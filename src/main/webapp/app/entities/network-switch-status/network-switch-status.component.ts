import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';
import { NetworkSwitchStatusService } from './network-switch-status.service';
import { NetworkSwitchStatusDeleteDialogComponent } from './network-switch-status-delete-dialog.component';

@Component({
  selector: 'jhi-network-switch-status',
  templateUrl: './network-switch-status.component.html',
})
export class NetworkSwitchStatusComponent implements OnInit, OnDestroy {
  networkSwitchStatuses?: INetworkSwitchStatus[];
  eventSubscriber?: Subscription;

  constructor(
    protected networkSwitchStatusService: NetworkSwitchStatusService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.networkSwitchStatusService
      .query()
      .subscribe((res: HttpResponse<INetworkSwitchStatus[]>) => (this.networkSwitchStatuses = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInNetworkSwitchStatuses();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: INetworkSwitchStatus): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInNetworkSwitchStatuses(): void {
    this.eventSubscriber = this.eventManager.subscribe('networkSwitchStatusListModification', () => this.loadAll());
  }

  delete(networkSwitchStatus: INetworkSwitchStatus): void {
    const modalRef = this.modalService.open(NetworkSwitchStatusDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.networkSwitchStatus = networkSwitchStatus;
  }
}
