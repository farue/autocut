import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';
import { NetworkSwitchDeleteDialogComponent } from './network-switch-delete-dialog.component';

@Component({
  selector: 'jhi-network-switch',
  templateUrl: './network-switch.component.html',
})
export class NetworkSwitchComponent implements OnInit, OnDestroy {
  networkSwitches?: INetworkSwitch[];
  eventSubscriber?: Subscription;

  constructor(
    protected networkSwitchService: NetworkSwitchService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.networkSwitchService.query().subscribe((res: HttpResponse<INetworkSwitch[]>) => (this.networkSwitches = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInNetworkSwitches();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: INetworkSwitch): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInNetworkSwitches(): void {
    this.eventSubscriber = this.eventManager.subscribe('networkSwitchListModification', () => this.loadAll());
  }

  delete(networkSwitch: INetworkSwitch): void {
    const modalRef = this.modalService.open(NetworkSwitchDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.networkSwitch = networkSwitch;
  }
}
