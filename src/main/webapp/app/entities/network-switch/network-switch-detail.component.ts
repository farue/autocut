import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INetworkSwitch } from 'app/shared/model/network-switch.model';

@Component({
  selector: 'jhi-network-switch-detail',
  templateUrl: './network-switch-detail.component.html'
})
export class NetworkSwitchDetailComponent implements OnInit {
  networkSwitch: INetworkSwitch | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ networkSwitch }) => {
      this.networkSwitch = networkSwitch;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
