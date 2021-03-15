import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';

@Component({
  selector: 'jhi-network-switch-status-detail',
  templateUrl: './network-switch-status-detail.component.html',
})
export class NetworkSwitchStatusDetailComponent implements OnInit {
  networkSwitchStatus: INetworkSwitchStatus | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitchStatus }) => (this.networkSwitchStatus = networkSwitchStatus));
  }

  previousState(): void {
    window.history.back();
  }
}
