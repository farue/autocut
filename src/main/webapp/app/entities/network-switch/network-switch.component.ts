import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { INetworkSwitch } from 'app/shared/model/network-switch.model';
import { NetworkSwitchService } from './network-switch.service';

@Component({
  selector: 'jhi-network-switch',
  templateUrl: './network-switch.component.html'
})
export class NetworkSwitchComponent implements OnInit, OnDestroy {
  networkSwitches: INetworkSwitch[];
  eventSubscriber: Subscription;

  constructor(protected networkSwitchService: NetworkSwitchService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.networkSwitchService.query().subscribe((res: HttpResponse<INetworkSwitch[]>) => {
      this.networkSwitches = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInNetworkSwitches();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: INetworkSwitch) {
    return item.id;
  }

  registerChangeInNetworkSwitches() {
    this.eventSubscriber = this.eventManager.subscribe('networkSwitchListModification', () => this.loadAll());
  }
}
