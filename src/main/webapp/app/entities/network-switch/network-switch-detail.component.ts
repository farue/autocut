import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INetworkSwitch } from 'app/shared/model/network-switch.model';

@Component({
  selector: 'jhi-network-switch-detail',
  templateUrl: './network-switch-detail.component.html'
})
export class NetworkSwitchDetailComponent implements OnInit {
  networkSwitch: INetworkSwitch;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => {
      this.networkSwitch = networkSwitch;
    });
  }

  previousState() {
    window.history.back();
  }
}
